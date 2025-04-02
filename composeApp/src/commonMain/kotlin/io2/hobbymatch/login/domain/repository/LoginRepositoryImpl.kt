package io2.hobbymatch.login.domain.repository

import io2.hobbymatch.login.data.local.realm.LoginMongoDB
import io2.hobbymatch.login.data.remote.LoginApiService
import io2.hobbymatch.login.data.remote.dto.TokenValidationRequest
import kotlinx.coroutines.flow.Flow

class LoginRepositoryImpl(
    private val loginApiService: LoginApiService,
    private val loginMongoDB: LoginMongoDB
) : LoginRepository {

    override suspend fun validateTokenWithBackend(idToken: String): Result<Boolean> {
        return try {
            val request = TokenValidationRequest(idToken = idToken)
            val response = loginApiService.validateGoogleToken(request)
            // TODO: Zapisz userId lub inne dane z response, jeśli są potrzebne
            Result.success(response.success)
        } catch (e: Exception) {
            println("Backend token validation failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun saveTokenLocally(token: String) {
        loginMongoDB.saveLoginToken(token)
    }

    override suspend fun loadTokenLocally(): String? {
        return loginMongoDB.loadLoginToken()
    }

    override fun getLocalTokenFlow(): Flow<String?> {
        return loginMongoDB.getLoginTokenFlow()
    }

    override suspend fun clearLocalToken() {
        loginMongoDB.resetLoginToken()
    }
}