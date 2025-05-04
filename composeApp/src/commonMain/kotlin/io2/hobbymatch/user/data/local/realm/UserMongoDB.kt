package io2.hobbymatch.user.data.local.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val SINGLE_USER_PROFILE_ID = "SINGLE_USER_PROFILE"

class UserMongoDB {
    private var realm: Realm? = null

    init {
        configureRealm()
    }

    private fun configureRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(UserProfileRealm::class, HobbyRealm::class)
            )
                .schemaVersion(2) // Increment this when schema changes
                .deleteRealmIfMigrationNeeded() // For development, will delete old data
                // Alternative for production: use migration
                /*.migration { dynamicRealm, oldVersion, newVersion ->
                    val schema = dynamicRealm.schema

                    if (oldVersion == 1L) {
                        schema.get("UserProfileRealm")?.apply {
                            // Transform hobbies property
                            transform { obj ->
                                // Migration logic here if needed
                            }
                        }
                    }
                }*/
                .compactOnLaunch()
                .build()

            try {
                realm = Realm.open(config)
            } catch (e: Exception) {
                // If opening fails, delete and recreate database (development only)
                Realm.deleteRealm(config)
                realm = Realm.open(config)
            }
        }
    }


    /**
     * Save the user profile into Realm, updating existing entries or creating new ones.
     */
    suspend fun saveUserProfile(userProfileData: UserProfileRealm) {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        withContext(Dispatchers.IO) {
            currentRealm.write {
                val existingProfile = this.query<UserProfileRealm>("id == $0", SINGLE_USER_PROFILE_ID).first().find()

                if (existingProfile != null) {
                    // Update existing profile
                    findLatest(existingProfile)?.apply {
                        name = userProfileData.name
                        email = userProfileData.email
                        hobbies.clear() // Clear existing hobbies
                        hobbies.addAll(userProfileData.hobbies) // Add updated hobbies
                    }
                } else {
                    // Create a new profile
                    userProfileData.id = SINGLE_USER_PROFILE_ID // Ensure correct ID is set
                    this.copyToRealm(userProfileData)
                }
            }
        }
    }

    /**
     * Load a user profile (one-time fetch).
     */
    suspend fun loadUserProfile(): UserProfileRealm? {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return withContext(Dispatchers.IO) {
            currentRealm.query<UserProfileRealm>("id == $0", SINGLE_USER_PROFILE_ID).first().find()
        }
    }

    /**
     * Watch the user profile as a Flow to handle changes in real time.
     */
    fun getUserProfileFlow(): Flow<UserProfileRealm?> {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        return currentRealm.query<UserProfileRealm>("id == $0", SINGLE_USER_PROFILE_ID)
            .first()
            .asFlow()
            .map { it.obj } // Convert the Realm query result into an appropriate type
    }

    /**
     * Close Realm instance when it's no longer needed.
     */
    fun close() {
        realm?.close()
        realm = null
    }
}