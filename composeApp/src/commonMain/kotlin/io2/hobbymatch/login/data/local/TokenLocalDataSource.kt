package io2.hobbymatch.login.data.local

import io2.hobbymatch.login.data.repository.AppJwt
import kotlinx.coroutines.flow.Flow

/**
 * Interface for storing/retrieving the application JWT locally.
 */
interface TokenLocalDataSource {
    suspend fun saveAppToken(token: AppJwt)
    suspend fun loadAppToken(): AppJwt?
    suspend fun clearAppToken()
    fun getAppTokenFlow(): Flow<AppJwt?>
}