package io2.hobbymatch.user.data.remote

import io2.hobbymatch.user.domain.UpdateUserRequest
import io2.hobbymatch.user.domain.User

interface UserApiService {
    suspend fun getAuthenticatedUser(): User
    suspend fun getUserById(userId: String): User
    suspend fun updateAuthenticatedUser(requestBody: UpdateUserRequest): User
    suspend fun updateUserById(userId: String, requestBody: UpdateUserRequest): User
}
