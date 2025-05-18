package io2.hobbymatch.auth.data.local.room

import io2.hobbymatch.auth.data.local.AuthLocalDataSource
import io2.hobbymatch.auth.domain.AuthResponse
import io2.hobbymatch.auth.domain.LoginDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRoomDataSource(private val authDao: AuthDao) : AuthLocalDataSource {

    override suspend fun saveLoginToken(token: String) = withContext(Dispatchers.IO) {
        val existingData = authDao.getAuthData()
        if (existingData != null) {
            authDao.insertOrUpdateAuth(existingData.copy(idToken = token))
        } else {
            authDao.insertOrUpdateAuth(AuthEntity(idToken = token))
        }
    }

    override suspend fun loadLoginToken(): String? = withContext(Dispatchers.IO) {
        authDao.getAuthData()?.idToken
    }

    override suspend fun resetLoginToken() = withContext(Dispatchers.IO) {
        authDao.deleteAuthData()
    }

    override suspend fun saveAuthResponse(authResponse: AuthResponse, role: String) = withContext(Dispatchers.IO) {
        val newData = AuthEntity(
            id = "LOGIN_DATA",
            accessToken = authResponse.accessToken,
            refreshToken = authResponse.refreshToken,
            userId = authResponse.loginInfo.id,
            email = authResponse.loginInfo.email,
            name = authResponse.loginInfo.name,
            role = role
        )

        authDao.insertOrUpdateAuth(newData)
    }

    override suspend fun loadRole(): String? = withContext(Dispatchers.IO) {
        authDao.getAuthData()?.role
    }

    override suspend fun loadAuthResponse(): AuthResponse? = withContext(Dispatchers.IO) {
        val data = authDao.getAuthData() ?: return@withContext null

        AuthResponse(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken,
            loginInfo = LoginDTO(
                id = data.userId,
                email = data.email,
                name = data.name
            )
        )
    }
}