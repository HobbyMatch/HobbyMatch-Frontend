package io2.hobbymatch.user.data

import io2.hobbymatch.user.data.local.realm.UserMongoDB
import io2.hobbymatch.user.data.local.realm.toDomainModel
import io2.hobbymatch.user.data.local.realm.toRealmObject
import io2.hobbymatch.user.data.remote.UserApiService
import io2.hobbymatch.user.domain.UpdateUserRequest
import io2.hobbymatch.user.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val apiService: UserApiService,
    private val localDB: UserMongoDB
) {
    /**
     * Fetch the authenticated user from API and update the local database.
     */
    suspend fun syncAuthenticatedUser(): User {
        val userFromApi = apiService.getAuthenticatedUser()
        // Save user to local storage
        localDB.saveUserProfile(userFromApi.toRealmObject())
        return userFromApi
    }

    /**
     * Load the authenticated user from local database.
     */
    suspend fun loadAuthenticatedUser(): User? {
        return localDB.loadUserProfile()?.toDomainModel()
    }

    /**
     * Observe changes to the user profile in the local database.
     */
    fun observeAuthenticatedUser(): Flow<User?> {
        return localDB.getUserProfileFlow()
            .map { it?.toDomainModel() }
    }

    /**
     * Update the authenticated user via the API and update the local database.
     * Returns the updated user object.
     */
    suspend fun updateAuthenticatedUser(user: User): User {
        // Convert User to UpdateUserRequest
        val updateRequest = UpdateUserRequest(
            name = user.name,
            email = user.email,
            hobbies = user.hobbies
        )
        // Update in API
        val updatedUserFromApi = apiService.updateAuthenticatedUser(updateRequest)
        // Save updates to local storage
        localDB.saveUserProfile(updatedUserFromApi.toRealmObject())
        return updatedUserFromApi
    }

    /**
     * Fetch a specific user by ID from the API.
     */
    suspend fun getUserById(userId: String): User {
        return apiService.getUser(userId)
    }

    /**
     * Update a specific user by ID via the API.
     * Doesn't save anything in the local database for other users.
     */
    suspend fun updateUser(userId: String, user: User): User {
        val updateRequest = UpdateUserRequest(
            name = user.name,
            email = user.email,
            hobbies = user.hobbies
        )
        return apiService.updateUser(userId, updateRequest)
    }
}