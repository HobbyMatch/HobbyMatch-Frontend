package io2.hobbymatch.login.data.repository

data class AppJwt(val value: String)

interface AuthRepository {
    suspend fun validateGoogleToken(googleIdToken: String): Result<AppJwt>
}