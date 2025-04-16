package io2.hobbymatch.login.data.repository

import io2.hobbymatch.login.data.local.TokenLocalDataSource
import kotlinx.coroutines.flow.Flow

class TokenRepositoryImpl(
    private val localDataSource: TokenLocalDataSource
) : TokenRepository {

    override suspend fun saveAppToken(token: AppJwt) {
        localDataSource.saveAppToken(token)
    }

    override suspend fun loadAppToken(): AppJwt? {
        return localDataSource.loadAppToken()
    }

    override suspend fun clearAppToken() {
        localDataSource.clearAppToken()
    }

    override fun getAppTokenFlow(): Flow<AppJwt?> {
        return localDataSource.getAppTokenFlow()
    }
}