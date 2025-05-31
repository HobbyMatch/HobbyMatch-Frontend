package io2.hobbymatch.auth.data

import io2.hobbymatch.auth.data.local.room.AuthRoomDataSource
import io2.hobbymatch.auth.data.remote.AuthApiService
import io2.hobbymatch.auth.data.remote.models.GoogleLoginRequest
import io2.hobbymatch.auth.data.remote.models.RefreshTokenRequest
import io2.hobbymatch.auth.domain.AuthResponse

class AuthRepository(
    private val authApiService: AuthApiService,
    private val authRoomDataSource: AuthRoomDataSource
) {
    suspend fun validateToken(token: String, role: String = "USER"): AuthResponse {
        println("Validating token: $token with role: $role ...")
        return authApiService.validateGoogleIdToken(token, role = role)
    }

    // Login with Google and store token locally
    suspend fun loginWithGoogle(idToken: String): AuthResponse {
        val response = authApiService.googleLogin(GoogleLoginRequest(idToken))
        authRoomDataSource.saveGoogleIdToken(response.accessToken)
        return response
    }

    // Refresh token
    suspend fun refreshToken(): AuthResponse {
        val refreshToken = authRoomDataSource.loadRefreshToken()
            ?: throw IllegalStateException("Refresh token is missing")

        val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))
        authRoomDataSource.saveGoogleIdToken(response.accessToken) // Update stored token
        return response
    }

    // Save token to local storage
    suspend fun saveToken(token: String) {
        authRoomDataSource.saveGoogleIdToken(token)
    }

    // Get the currently stored token
    suspend fun loadAccessToken(): String? {
        return authRoomDataSource.loadAccessToken()
    }

    suspend fun loadRefreshToken(): String? {
        return authRoomDataSource.loadRefreshToken()
    }

    // Logout (clears all stored auth data)
    suspend fun logout() {
        authRoomDataSource.resetAllAuthData()
    }

    suspend fun saveAuthResponse(authResponse: AuthResponse, role: String = "USER") {
        authRoomDataSource.saveAuthResponse(authResponse, role)
    }

    suspend fun loadRole(): String? {
        return authRoomDataSource.loadRole()
    }

    suspend fun loadAuthResponse(): AuthResponse? {
        return authRoomDataSource.loadAuthResponse()
    }
}