package io2.hobbymatch.user.data.remote

import io2.hobbymatch.user.domain.User

interface UserApiService {
    suspend fun getAuthenticatedUser(): User
    suspend fun getUser(userId: String): User
    suspend fun updateAuthenticatedUser(user: User)
    suspend fun updateUser(userId: String, user: User)
}
