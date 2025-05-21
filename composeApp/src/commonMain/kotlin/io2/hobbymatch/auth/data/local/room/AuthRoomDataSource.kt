package io2.hobbymatch.auth.data.local.room

import io2.hobbymatch.auth.domain.AuthResponse
import io2.hobbymatch.auth.domain.LoginDTO
import kotlinx.coroutines.flow.Flow

class AuthRoomDataSource(private val authDao: AuthDao) {

    suspend fun saveGoogleIdToken(googleIdToken: String) {
        val existingData = authDao.getAuthData()
        if (existingData != null) {
            authDao.insertOrUpdateAuth(existingData.copy(googleIdToken = googleIdToken))
        } else {
            authDao.insertOrUpdateAuth(AuthEntity(googleIdToken = googleIdToken))
        }
    }

    suspend fun loadAccessToken(): String? {
        return authDao.getAuthData()?.googleIdToken ?: authDao.getAuthData()?.accessToken
    }

    suspend fun loadRefreshToken(): String? {
        return authDao.getAuthData()?.googleIdToken ?: authDao.getAuthData()?.refreshToken
    }

    suspend fun resetAllAuthData() {
        authDao.deleteAuthData()
    }

    suspend fun saveAuthResponse(authResponse: AuthResponse, role: String) {
        val existingData = authDao.getAuthData()
        val existingGoogleIdToken = existingData?.googleIdToken

        val newData = AuthEntity(
            id = "LOGIN_DATA",
            googleIdToken = existingGoogleIdToken ?: authResponse.accessToken,
            accessToken = authResponse.accessToken,
            refreshToken = authResponse.refreshToken,
            userId = authResponse.loginInfo.id,
            email = authResponse.loginInfo.email,
            name = authResponse.loginInfo.name,
            role = role
        )

        authDao.insertOrUpdateAuth(newData)
    }

    suspend fun loadRole(): String? {
        return authDao.getAuthData()?.role
    }

    suspend fun loadAuthResponse(): AuthResponse? {
        val data = authDao.getAuthData() ?: return null

        return AuthResponse(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken,
            loginInfo = LoginDTO(
                id = data.userId,
                email = data.email,
                name = data.name
            )
        )
    }

    fun getLoginTokenFlow(): Flow<String?> {
        return authDao.getLoginTokenFlow()
    }
}