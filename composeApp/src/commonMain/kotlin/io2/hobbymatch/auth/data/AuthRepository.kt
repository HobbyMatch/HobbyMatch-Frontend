package io2.hobbymatch.auth.data

import io2.hobbymatch.auth.data.local.realm.LoginMongoDB
import io2.hobbymatch.auth.data.remote.AuthApiService
import io2.hobbymatch.auth.data.remote.models.GoogleLoginRequest
import io2.hobbymatch.auth.data.remote.models.RefreshTokenRequest
import io2.hobbymatch.auth.domain.AuthResponse

class AuthRepository(
    private val authApiService: AuthApiService,
    private val loginMongoDB: LoginMongoDB
) {
    suspend fun validateToken(token: String, role: String = "USER"): AuthResponse {
        return authApiService.validateGoogleIdToken(token)
    }

    // Login with Google and store token locally
    suspend fun loginWithGoogle(idToken: String): AuthResponse {
        val response = authApiService.googleLogin(GoogleLoginRequest(idToken))
        loginMongoDB.saveLoginToken(response.accessToken)
        return response
    }

    // Refresh token
    suspend fun refreshToken(): AuthResponse {
        val refreshToken = loginMongoDB.loadLoginToken()
            ?: throw IllegalStateException("Refresh token is missing")

        val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))
        loginMongoDB.saveLoginToken(response.accessToken) // Update stored token
        return response
    }

    // Save token to local storage
    suspend fun saveToken(token: String) {
        loginMongoDB.saveLoginToken(token)
    }


    // Get the currently stored token
    suspend fun loadToken(): String? {
        return loginMongoDB.loadLoginToken()
    }

    // Logout (clears stored token)
    suspend fun logout() {
        loginMongoDB.resetLoginToken()
    }
}
