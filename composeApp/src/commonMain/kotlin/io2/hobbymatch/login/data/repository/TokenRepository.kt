package io2.hobbymatch.login.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing the locally stored application JWT.
 */
interface TokenRepository {
    /**
     * Saves the application's JWT securely to local storage.
     * @param token The application JWT to save.
     */
    suspend fun saveAppToken(token: AppJwt)

    /**
     * Loads the application's JWT from local storage.
     * @return The saved AppJwt, or null if none exists.
     */
    suspend fun loadAppToken(): AppJwt?

    /**
     * Clears the application's JWT from local storage (logout).
     */
    suspend fun clearAppToken()

    /**
     * Gets a flow that emits the application JWT whenever it changes.
     * Useful for observing login status reactively.
     * @return A Flow emitting the AppJwt or null.
     */
    fun getAppTokenFlow(): Flow<AppJwt?>
}