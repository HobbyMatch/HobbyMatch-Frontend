package io2.hobbymatch.auth.data.local.room

import io2.hobbymatch.auth.domain.AuthResponse
import io2.hobbymatch.auth.domain.LoginDTO
import kotlinx.coroutines.flow.Flow

class AuthRoomDataSource(private val authDao: AuthDao) {

    suspend fun saveLoginToken(token: String) {
        val existingData = authDao.getAuthData()
        if (existingData != null) {
            authDao.insertOrUpdateAuth(existingData.copy(token = token, accessToken = token))
        } else {
            authDao.insertOrUpdateAuth(AuthEntity(token = token, accessToken = token))
        }
    }

    suspend fun loadLoginToken(): String? {
        return authDao.getAuthData()?.token ?: authDao.getAuthData()?.accessToken
    }

    suspend fun resetLoginToken() {
        authDao.deleteAuthData()
    }

    suspend fun saveAuthResponse(authResponse: AuthResponse, role: String) {
        val newData = AuthEntity(
            id = "LOGIN_DATA",
            token = authResponse.accessToken,
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