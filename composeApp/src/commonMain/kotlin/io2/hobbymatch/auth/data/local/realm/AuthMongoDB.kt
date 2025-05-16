package io2.hobbymatch.auth.data.local.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.SingleQueryChange
import io2.hobbymatch.auth.domain.AuthResponse
import io2.hobbymatch.auth.domain.LoginDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// Define a constant for the LoginDataRealm object ID
private const val LOGIN_DATA_ID = "LOGIN_DATA" // From your LoginDataRealm class

class AuthMongoDB {
    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    private fun configureTheRealm() {
        if (realm == null || realm?.isClosed() == false) {
            val config = RealmConfiguration.Builder(
                schema = setOf(AuthDataRealm::class)
            )
                .name("auth.realm")
                .compactOnLaunch()
                .deleteRealmIfMigrationNeeded()
                .build()
            realm = Realm.open(config)
        }
    }

    suspend fun saveLoginToken(token: String) {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        withContext(Dispatchers.IO) {
            currentRealm.write {
                val existingLoginData: AuthDataRealm? =
                    this.query<AuthDataRealm>("id == $0", LOGIN_DATA_ID).first().find()

                if (existingLoginData != null) {
                    // Update existing
                    findLatest(existingLoginData)?.idToken = token // Or throw error
                } else {
                    // Create new
                    this.copyToRealm(AuthDataRealm().apply {
                        this.id = LOGIN_DATA_ID
                        this.idToken = token
                    })
                }
            }
        }
    }

    suspend fun loadLoginToken(): String? {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return withContext(Dispatchers.IO) {
            val loginData = currentRealm.query<AuthDataRealm>("id == $0", LOGIN_DATA_ID).first().find()
            loginData?.idToken // Return token or null
        }
    }

    // --- Get Login Token Flow (Optional but recommended) ---
    fun getLoginTokenFlow(): Flow<String?> {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return currentRealm.query<AuthDataRealm>("id == $0", LOGIN_DATA_ID)
            .first()
            .asFlow()
            .map { change: SingleQueryChange<AuthDataRealm> ->
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
                val loginDataToDelete: AuthDataRealm? =
                    this.query<AuthDataRealm>("id == $0", LOGIN_DATA_ID).first().find()

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

    suspend fun saveAuthResponse(authResponse: AuthResponse, role: String) {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        withContext(Dispatchers.IO) {
            currentRealm.write {
                val existingData = this.query<AuthDataRealm>("id == $0", LOGIN_DATA_ID).first().find()
                if (existingData != null) {
                    findLatest(existingData)?.apply {
                        accessToken = authResponse.accessToken
                        refreshToken = authResponse.refreshToken
                        userId = authResponse.loginInfo.id
                        email = authResponse.loginInfo.email
                        name = authResponse.loginInfo.name
                        this.role = role // Zapis roli
                    }
                } else {
                    this.copyToRealm(AuthDataRealm().apply {
                        id = LOGIN_DATA_ID
                        accessToken = authResponse.accessToken
                        refreshToken = authResponse.refreshToken
                        userId = authResponse.loginInfo.id
                        email = authResponse.loginInfo.email
                        name = authResponse.loginInfo.name
                        this.role = role // Zapis roli
                    })
                }
            }
        }
    }

    suspend fun loadRole(): String? {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return withContext(Dispatchers.IO) {
            val data = currentRealm.query<AuthDataRealm>("id == $0", LOGIN_DATA_ID).first().find()
            data?.role // Zwraca zapisaną rolę lub null
        }
    }

    suspend fun loadAuthResponse(): AuthResponse? {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return withContext(Dispatchers.IO) {
            val data = currentRealm.query<AuthDataRealm>("id == $0", LOGIN_DATA_ID).first().find()
            data?.let {
                AuthResponse(
                    accessToken = it.accessToken,
                    refreshToken = it.refreshToken,
                    loginInfo = LoginDTO(
                        id = it.userId,
                        email = it.email,
                        name = it.name
                    )
                )
            }
        }
    }

    fun close() {
        realm?.close()
        realm = null
    }
}