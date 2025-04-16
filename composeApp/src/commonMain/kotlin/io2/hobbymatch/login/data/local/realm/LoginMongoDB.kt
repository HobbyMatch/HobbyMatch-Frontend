package io2.hobbymatch.login.data.local.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.SingleQueryChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// Define a constant for the LoginDataRealm object ID
private const val LOGIN_DATA_ID = "LOGIN_DATA" // From your LoginDataRealm class

class LoginMongoDB {
    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    private fun configureTheRealm() {
        if (realm == null || realm?.isClosed() == false) { // Check if not closed
            val config = RealmConfiguration.Builder(
                // Include LoginDataRealm in the schema
                schema = setOf(LoginDataRealm::class)
            )
                .name("login.realm") // Optional: Give it a specific name
                .compactOnLaunch()
                .deleteRealmIfMigrationNeeded()
                .build()
            realm = Realm.open(config)
        }
    }

    // --- Save Login Token ---
    suspend fun saveLoginToken(token: String) {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        withContext(Dispatchers.IO) {
            currentRealm.write {
                val existingLoginData: LoginDataRealm? =
                    this.query<LoginDataRealm>("id == $0", LOGIN_DATA_ID).first().find()

                if (existingLoginData != null) {
                    // Update existing
                    findLatest(existingLoginData)?.idToken = token // Or throw error
                } else {
                    // Create new
                    this.copyToRealm(LoginDataRealm().apply {
                        this.id = LOGIN_DATA_ID
                        this.idToken = token
                    })
                }
            }
        }
    }

    // --- Save Login Token ---
    suspend fun saveJwtToken(jwtToken: String) {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        withContext(Dispatchers.IO) {
            currentRealm.write {
                val existingLoginData: LoginDataRealm? =
                    this.query<LoginDataRealm>("id == $0", LOGIN_DATA_ID).first().find()

                if (existingLoginData != null) {
                    // Update existing
                    findLatest(existingLoginData)?.jwtToken = jwtToken // Or throw error
                } else {
                    // Create new
                    this.copyToRealm(LoginDataRealm().apply {
                        this.id = LOGIN_DATA_ID
                        this.jwtToken = jwtToken
                    })
                }
            }
        }
    }

    suspend fun loadLoginToken(): String? {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return withContext(Dispatchers.IO) {
            val loginData = currentRealm.query<LoginDataRealm>("id == $0", LOGIN_DATA_ID).first().find()
            loginData?.idToken // Return token or null
        }
    }

    suspend fun loadJwtToken(): String? {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return withContext(Dispatchers.IO) {
            val loginData = currentRealm.query<LoginDataRealm>("id == $0", LOGIN_DATA_ID).first().find()
            loginData?.jwtToken // Return token or null
        }
    }

    // --- Get Login Token Flow (Optional but recommended) ---
    fun getLoginTokenFlow(): Flow<String?> {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return currentRealm.query<LoginDataRealm>("id == $0", LOGIN_DATA_ID)
            .first()
            .asFlow()
            .map { change: SingleQueryChange<LoginDataRealm> ->
                change.obj?.idToken // Map to the token string or null
            }
    }

    // --- Reset Login Token ---
    // Deletes the LoginDataRealm object, effectively clearing the saved token.
    suspend fun resetLoginToken() {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        withContext(Dispatchers.IO) {
            currentRealm.write {
                // Query for the object to delete
                val loginDataToDelete: LoginDataRealm? =
                    this.query<LoginDataRealm>("id == $0", LOGIN_DATA_ID).first().find()

                // If found, find the latest version in this transaction and delete it
                loginDataToDelete?.let { foundObject ->
                    findLatest(foundObject)?.also { latestVersion ->
                        delete(latestVersion)
                        println("Login token reset (LoginDataRealm object deleted).") // Optional log
                    } ?: println("Could not find latest version of LoginDataRealm to delete.")
                } ?: println("Login token already reset (LoginDataRealm object not found).") // Optional log
            }
        }
    }

    private fun ensureRealmOpen(): Realm {
        // Prosta metoda zapewniająca, że realm jest otwarty przed użyciem
        configureTheRealm() // Upewnia się, że jest instancja
        return realm ?: throw IllegalStateException("Realm initialization failed.")
    }

    // --- NOWA METODA: Get Application JWT Token Flow ---
    fun getJwtTokenFlow(): Flow<String?> {
        val currentRealm = ensureRealmOpen()
        // Analogicznie do getLoginTokenFlow, ale obserwujemy pole jwtToken
        return currentRealm.query<LoginDataRealm>("id == $0", LOGIN_DATA_ID)
            .first()
            .asFlow()
            .map { change: SingleQueryChange<LoginDataRealm> ->
                change.obj?.jwtToken?.ifBlank { null } // Mapuj na String? i traktuj pusty jako null
            }
    }

    // --- NOWA METODA: Clear Application JWT Token ---
    // Czyści tylko pole jwtToken, pozostawiając obiekt (i idToken).
    suspend fun clearJwtToken() {
        val currentRealm = ensureRealmOpen()
        withContext(Dispatchers.IO) {
            currentRealm.write {
                val loginDataToUpdate: LoginDataRealm? =
                    this.query<LoginDataRealm>("id == $0", LOGIN_DATA_ID).first().find()

                // Jeśli obiekt istnieje, znajdź najnowszą wersję i wyczyść pole
                loginDataToUpdate?.let { foundObject ->
                    findLatest(foundObject)?.also { latestVersion ->
                        latestVersion.jwtToken = "" // Ustaw na pusty string
                        println("App JWT token cleared.") // Opcjonalny log
                    }
                } ?: println("App JWT token already cleared (LoginDataRealm object not found or field empty).")
            }
        }
    }

    fun close() {
        realm?.close()
        realm = null
    }
}