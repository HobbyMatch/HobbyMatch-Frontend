package io2.hobbymatch.user.data.local.room

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRoomDataSource(private val db: UserDatabase) {

    private val userDao = db.userDao()
    private val defaultId = "SINGLE_USER_PROFILE"

    suspend fun saveUserProfile(id: String = defaultId, name: String, email: String, hobbies: List<String>) = withContext(Dispatchers.IO) {
        val userProfile = UserProfileEntity(id = id, name = name, email = email)
        val hobbyEntities = hobbies.map { HobbyEntity(name = it, userProfileId = id) }
        userDao.saveUserProfileWithHobbies(userProfile, hobbyEntities)
    }

    suspend fun loadUserProfile(id: String = defaultId): UserProfileWithHobbies? = withContext(Dispatchers.IO) {
        userDao.getUserProfileWithHobbies(id)
    }

    fun getUserProfileFlow(): Flow<UserProfileWithHobbies?> {
        return userDao.getUserProfileWithHobbiesFlow(defaultId)
    }
}