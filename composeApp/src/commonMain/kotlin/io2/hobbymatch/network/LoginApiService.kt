package io2.hobbymatch.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io2.hobbymatch.login.data.remote.model.AuthResponse
import io2.hobbymatch.login.data.remote.model.GoogleTokenRequest

class LoginApiService(private val httpClient: HttpClient) {

    // TODO: Replace Hardcoded URL
    companion object {
        private const val BASE_URL = "http://localhost:8080" // Replace with your actual backend URL
        private const val AUTH_ENDPOINT = "$BASE_URL/auth/mobile/google"
    }

    suspend fun authenticateWithGoogle(googleIdToken: String): Result<AuthResponse> {
        return try {
            val response = httpClient.post(AUTH_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(GoogleTokenRequest(googleIdToken))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
