package io2.hobbymatch.auth.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io2.hobbymatch.auth.data.remote.models.GoogleLoginRequest
import io2.hobbymatch.auth.data.remote.models.RefreshTokenRequest
import io2.hobbymatch.auth.domain.AuthResponse
import io2.hobbymatch.auth.domain.UserInfo
import io2.hobbymatch.network.ApiConfig

class AuthApiServiceImpl(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : AuthApiService {
    override suspend fun validateGoogleIdToken(token: String, role: String): AuthResponse {
        val body = mapOf("idToken" to token, "role" to role)

        return httpClient.post {
            url(apiConfig.getEndpoint(AuthApiEndpoints.GOOGLE_LOGIN)) // Example: Add the validation endpoint
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body() // Parse the response body into an AuthResponse
    }


    override suspend fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        return httpClient.post {
            url(apiConfig.getEndpoint(AuthApiEndpoints.REFRESH))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun googleLogin(request: GoogleLoginRequest): AuthResponse {
        return httpClient.post {
            url(apiConfig.getEndpoint(AuthApiEndpoints.GOOGLE_LOGIN))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getUserInfo(): UserInfo {
        return httpClient.get {
            url(apiConfig.getEndpoint(AuthApiEndpoints.ME))
        }.body()
    }
}
