package io2.hobbymatch.auth.data

import io2.hobbymatch.auth.data.local.realm.AuthMongoDB
import io2.hobbymatch.auth.data.remote.AuthApiService
import io2.hobbymatch.auth.data.remote.models.GoogleLoginRequest
import io2.hobbymatch.auth.data.remote.models.RefreshTokenRequest
import io2.hobbymatch.auth.domain.AuthResponse

class AuthRepository(
    private val authApiService: AuthApiService,
    private val authMongoDB: AuthMongoDB
) {
    suspend fun validateToken(token: String, role: String = "USER"): AuthResponse {
        return authApiService.validateGoogleIdToken(token, role = role)
    }

    // Login with Google and store token locally
    suspend fun loginWithGoogle(idToken: String): AuthResponse {
        val response = authApiService.googleLogin(GoogleLoginRequest(idToken))
        authMongoDB.saveLoginToken(response.accessToken)
        return response
    }

    // Refresh token
    suspend fun refreshToken(): AuthResponse {
        val refreshToken = authMongoDB.loadLoginToken()
            ?: throw IllegalStateException("Refresh token is missing")

        val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))
        authMongoDB.saveLoginToken(response.accessToken) // Update stored token
        return response
    }

    // Save token to local storage
    suspend fun saveToken(token: String) {
        authMongoDB.saveLoginToken(token)
    }


    // Get the currently stored token
    suspend fun loadToken(): String? {
        return authMongoDB.loadLoginToken()
    }

    // Logout (clears stored token)
    suspend fun logout() {
        authMongoDB.resetLoginToken()
    }
}
