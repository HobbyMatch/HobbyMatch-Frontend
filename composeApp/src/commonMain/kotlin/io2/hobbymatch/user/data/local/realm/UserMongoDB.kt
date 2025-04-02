package io2.hobbymatch.user.data.local.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.SingleQueryChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// Define a constant for the profile ID
private const val SINGLE_USER_PROFILE_ID = "SINGLE_USER_PROFILE"

class UserMongoDB {
    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    private fun configureTheRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(UserProfileRealm::class)
            )
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

    // --- Save User Profile ---
    // Takes a UserProfileRealm object representing the desired state
    suspend fun saveUserProfile(userProfileData: UserProfileRealm) {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        // Ensure write operations happen on a background thread if called from Main
        withContext(Dispatchers.IO) {
            currentRealm.write {
                // Query for the existing profile using the constant ID
                val existingProfile: UserProfileRealm? =
                    this.query<UserProfileRealm>("id == $0", SINGLE_USER_PROFILE_ID).first().find()

                if (existingProfile != null) {
                    // --- Update Existing Profile ---
                    // Find the managed version of the existing profile to update
                    findLatest(existingProfile)?.apply {
                        email = userProfileData.email
                        username = userProfileData.username
                        name = userProfileData.name
                        surname = userProfileData.surname
                        birthday = userProfileData.birthday
                        gender = userProfileData.gender
                        bio = userProfileData.bio
                        // Update hobbies list: Clear existing and add current ones
                        hobbies.clear()
                        hobbies.addAll(userProfileData.hobbies)
                    }
                } else {
                    // --- Create New Profile ---
                    // Ensure the ID is set correctly when creating
                    userProfileData.id = SINGLE_USER_PROFILE_ID
                    this.copyToRealm(userProfileData)
                }
            }
        }
    }

    // --- Load User Profile (One-time) ---
    // Returns the profile or null if not found
    suspend fun loadUserProfile(): UserProfileRealm? {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        // Ensure query happens on a background thread if called from Main
        return withContext(Dispatchers.IO) {
            currentRealm.query<UserProfileRealm>("id == $0", SINGLE_USER_PROFILE_ID).first().find()
        }
    }

    // --- Get User Profile as a Flow ---
    // Emits the profile whenever it changes, or null if it doesn't exist / is deleted
    fun getUserProfileFlow(): Flow<UserProfileRealm?> {
        val currentRealm = realm ?: throw IllegalStateException("Realm is not initialized.")
        // Realm flows operate on their own background thread dispatcher
        return currentRealm.query<UserProfileRealm>("id == $0", SINGLE_USER_PROFILE_ID)
            .first() // Query for a single object
            .asFlow() // Get it as a Flow emitting SingleQueryChange
            .map { change: SingleQueryChange<UserProfileRealm> ->
                // Access the object via the .obj property of SingleQueryChange
                change.obj // This will be the UserProfileRealm object or null
            }
    }

    fun close() {
        realm?.close()
        realm = null
    }
}