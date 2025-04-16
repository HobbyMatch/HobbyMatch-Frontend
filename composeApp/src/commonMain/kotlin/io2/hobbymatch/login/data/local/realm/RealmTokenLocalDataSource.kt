package io2.hobbymatch.login.data.local.realm

import io2.hobbymatch.login.data.local.TokenLocalDataSource
import io2.hobbymatch.login.data.repository.AppJwt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealmTokenLocalDataSource(
    private val loginMongoDB: LoginMongoDB // Assume LoginMongoDB handles Realm operations
) : TokenLocalDataSource {

    override suspend fun saveAppToken(token: AppJwt) {
        loginMongoDB.saveJwtToken(token.value) // Adapt based on LoginMongoDB's API
    }

    override suspend fun loadAppToken(): AppJwt? {
        // Adapt based on LoginMongoDB's API - assuming it returns String?
        return loginMongoDB.loadJwtToken()?.let { AppJwt(it) }
    }

    override suspend fun clearAppToken() {
        loginMongoDB.clearJwtToken() // Adapt based on LoginMongoDB's API
    }

    override fun getAppTokenFlow(): Flow<AppJwt?> {
        // Adapt based on LoginMongoDB's API - assuming it provides a Flow<String?>
        return loginMongoDB.getJwtTokenFlow().map { tokenString ->
            tokenString?.let { AppJwt(it) }
        }
    }
}