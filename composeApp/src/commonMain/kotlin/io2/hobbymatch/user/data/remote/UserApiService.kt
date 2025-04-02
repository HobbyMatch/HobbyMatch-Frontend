// io2/hobbymatch/user/data/remote/UserApiService.kt
package io2.hobbymatch.user.data.remote

import io2.hobbymatch.user.data.remote.dto.UserProfileDto

interface UserApiService {
    // Zwraca np. Result<Unit> lub rzuca wyjątek w razie błędu
    suspend fun updateUserProfile(userId: String, profile: UserProfileDto)
    // Możesz dodać funkcję do pobierania profilu, jeśli potrzebne
    // suspend fun getUserProfile(userId: String): UserProfileDto
}