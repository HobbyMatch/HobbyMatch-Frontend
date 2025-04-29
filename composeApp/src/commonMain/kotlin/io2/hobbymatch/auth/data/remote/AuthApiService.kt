package io2.hobbymatch.auth.data.remote

import io2.hobbymatch.auth.data.remote.models.GoogleLoginRequest
import io2.hobbymatch.auth.data.remote.models.RefreshTokenRequest
import io2.hobbymatch.auth.domain.AuthResponse
import io2.hobbymatch.auth.domain.UserInfo

interface AuthApiService {
    suspend fun refreshToken(request: RefreshTokenRequest): AuthResponse
    suspend fun googleLogin(request: GoogleLoginRequest): AuthResponse
    suspend fun getUserInfo(): UserInfo
}