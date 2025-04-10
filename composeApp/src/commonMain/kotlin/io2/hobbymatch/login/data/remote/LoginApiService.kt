package io2.hobbymatch.login.data.remote

import io2.hobbymatch.login.data.remote.dto.TokenValidationRequest
import io2.hobbymatch.login.data.remote.dto.TokenValidationResponse

interface LoginApiService {
    suspend fun validateGoogleToken(request: TokenValidationRequest): TokenValidationResponse
}
