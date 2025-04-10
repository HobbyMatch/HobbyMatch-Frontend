package io2.hobbymatch.login.data.local.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.SingleQueryChange
import io2.hobbymatch.network.TokenStorage
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
                    findLatest(existingLoginData)?.token = token // Or throw error
                } else {
                    // Create new
                    this.copyToRealm(LoginDataRealm().apply {
                        this.id = LOGIN_DATA_ID
                        this.token = token
                    })
                }

                // Also update the TokenStorage
                TokenStorage.setToken(token)
            }
        }
    }

    // --- Load Login Token (One-time) ---
    suspend fun loadLoginToken(): String? {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return withContext(Dispatchers.IO) {
            val loginData = currentRealm.query<LoginDataRealm>("id == $0", LOGIN_DATA_ID).first().find()
            loginData?.token // Return token or null
        }
    }

    // --- Get Login Token Flow (Optional but recommended) ---
    fun getLoginTokenFlow(): Flow<String?> {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return currentRealm.query<LoginDataRealm>("id == $0", LOGIN_DATA_ID)
            .first()
            .asFlow()
            .map { change: SingleQueryChange<LoginDataRealm> ->
                change.obj?.token // Map to the token string or null
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

    fun close() {
        realm?.close()
        realm = null
    }
}