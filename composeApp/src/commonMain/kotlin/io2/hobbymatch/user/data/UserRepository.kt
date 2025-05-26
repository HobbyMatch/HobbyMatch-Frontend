package io2.hobbymatch.user.data

import io2.hobbymatch.hobby.domain.Hobby
import io2.hobbymatch.user.data.local.room.UserRoomDataSource
import io2.hobbymatch.user.data.remote.UserApiService
import io2.hobbymatch.user.domain.UpdateUserRequest
import io2.hobbymatch.user.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val apiService: UserApiService,
    private val userRoomDataSource: UserRoomDataSource
) {
    /**
     * Fetch the authenticated user from API and update the local database.
     */
    suspend fun syncAuthenticatedUser(): User {
        val userFromApi = apiService.getAuthenticatedUser()
        // Save user to local storage
        userRoomDataSource.saveUserProfile(
            name = userFromApi.name,
            email = userFromApi.email,
            hobbies = userFromApi.hobbies.map { it.name }
        )
        return userFromApi
    }

    /**
     * Load the authenticated user from local database.
     */
    suspend fun loadAuthenticatedUser(): User? {
        return userRoomDataSource.loadUserProfile()?.let {
            User(
                id = it.userProfile.id,
                name = it.userProfile.name,
                email = it.userProfile.email,
                hobbies = it.hobbies.map { hobby -> Hobby(hobby.name) }
            )
        }
    }

    /**
     * Observe changes to the user profile in the local database.
     */
    fun observeAuthenticatedUser(): Flow<User?> {
        return userRoomDataSource.getUserProfileFlow().map {
                userProfileWithHobbies ->
            userProfileWithHobbies?.let {
                User(
                    id = it.userProfile.id,
                    name = it.userProfile.name,
                    email = it.userProfile.email,
                    hobbies = it.hobbies.map { hobby -> Hobby(hobby.name) }
                )
            }
        }
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
        userRoomDataSource.saveUserProfile(
            name = updatedUserFromApi.name,
            email = updatedUserFromApi.email,
            hobbies = updatedUserFromApi.hobbies.map { it.name }
        )
        return updatedUserFromApi
    }

    /**
     * Fetch a specific user by ID from the API.
     */
    suspend fun getUserById(userId: String): User {
        return apiService.getUserById(userId)
    }

    /**
     * Update a specific user by ID via the API.
     * Doesn't save anything in the local database for other users.
     */
    suspend fun updateUserById(userId: String, user: User): User {
        val updateRequest = UpdateUserRequest(
            name = user.name,
            email = user.email,
            hobbies = user.hobbies
        )
        return apiService.updateUserById(userId, updateRequest)
    }
}