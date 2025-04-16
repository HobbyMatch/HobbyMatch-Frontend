package io2.hobbymatch.login.data.remote

import io2.hobbymatch.login.domain.AuthResponse

/**
 * Interface for remote authentication operations.
 */
interface AuthRemoteDataSource {
    /**
     * Sends the Google ID token to the backend for validation.
     * @param googleIdToken The token from Google Sign-In.
     * @return Result containing the backend's AuthResponse (with app JWT) or an exception.
     */
    suspend fun validateGoogleToken(googleIdToken: String): Result<AuthResponse>
}