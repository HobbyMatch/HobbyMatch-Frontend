package io2.hobbymatch.auth.data

import io2.hobbymatch.auth.data.local.room.RoomAuthDataSource
import io2.hobbymatch.auth.data.remote.AuthApiService
import io2.hobbymatch.auth.data.remote.models.GoogleLoginRequest
import io2.hobbymatch.auth.data.remote.models.RefreshTokenRequest
import io2.hobbymatch.auth.domain.AuthResponse

class AuthRepository(
    private val authApiService: AuthApiService,
    private val roomAuthDataSource: RoomAuthDataSource
) {
    suspend fun validateToken(token: String, role: String = "USER"): AuthResponse {
        return authApiService.validateGoogleIdToken(token, role = role)
    }

    // Login with Google and store token locally
    suspend fun loginWithGoogle(idToken: String): AuthResponse {
        val response = authApiService.googleLogin(GoogleLoginRequest(idToken))
        roomAuthDataSource.saveLoginToken(response.accessToken)
        return response
    }

    // Refresh token
    suspend fun refreshToken(): AuthResponse {
        val refreshToken = roomAuthDataSource.loadLoginToken()
            ?: throw IllegalStateException("Refresh token is missing")

        val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))
        roomAuthDataSource.saveLoginToken(response.accessToken) // Update stored token
        return response
    }

    // Save token to local storage
    suspend fun saveToken(token: String) {
        roomAuthDataSource.saveLoginToken(token)
    }

    // Get the currently stored token
    suspend fun loadToken(): String? {
        return roomAuthDataSource.loadLoginToken()
    }

    // Logout (clears stored token)
    suspend fun logout() {
        roomAuthDataSource.resetLoginToken()
    }

    suspend fun saveAuthResponse(authResponse: AuthResponse, role: String = "USER") {
        roomAuthDataSource.saveAuthResponse(authResponse, role)
    }

    suspend fun loadRole(): String? {
        return roomAuthDataSource.loadRole()
    }

    suspend fun loadAuthResponse(): AuthResponse? {
        return roomAuthDataSource.loadAuthResponse()
    }
}