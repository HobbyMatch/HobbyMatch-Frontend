package io2.hobbymatch.login.data.repository

import io2.hobbymatch.login.data.remote.AuthRemoteDataSource

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun validateGoogleToken(googleIdToken: String): Result<AppJwt> {
        return remoteDataSource.validateGoogleToken(googleIdToken).mapCatching { authResponse ->
            AppJwt(authResponse.token)
        }
    }
}