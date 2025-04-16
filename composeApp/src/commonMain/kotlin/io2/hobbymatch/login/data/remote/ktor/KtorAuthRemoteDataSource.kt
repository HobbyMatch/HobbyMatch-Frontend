package io2.hobbymatch.login.data.remote.ktor

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io2.hobbymatch.login.data.remote.AuthRemoteDataSource
import io2.hobbymatch.login.domain.AuthResponse
import kotlinx.serialization.Serializable

@Serializable
private data class GoogleTokenRequestDto(val idToken: String)

class KtorAuthRemoteDataSource(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : AuthRemoteDataSource {

    override suspend fun validateGoogleToken(googleIdToken: String): Result<AuthResponse> {
        return try {
            println("Attempting to validate Google token with backend: $baseUrl/auth/mobile/google") // Zastąpione logowanie
            val response = httpClient.post("$baseUrl/auth/mobile/google") {
                contentType(ContentType.Application.Json)
                setBody(GoogleTokenRequestDto(idToken = googleIdToken))
            }
            if (response.status.isSuccess()) {
                println("Backend validation successful (Status: ${response.status.value})") // Zastąpione logowanie
                Result.success(response.body<AuthResponse>())
            } else {
                println("Backend validation failed (Status: ${response.status.value})") // Zastąpione logowanie
                Result.failure(Exception("Backend validation failed with status: ${response.status.value}"))
            }
        } catch (e: Exception) {
            println("Exception during backend validation: ${e::class.simpleName} - ${e.message}") // Zastąpione logowanie
            Result.failure(e)
        }
    }
}

//class KtorAuthRemoteDataSource(
//    private val httpClient: HttpClient,
//    private val baseUrl: String // Inject base URL
//) : AuthRemoteDataSource {
//
//    override suspend fun validateGoogleToken(googleIdToken: String): Result<AuthResponse> {
//        return try {
//            val response = httpClient.post("$baseUrl/auth/mobile/google") {
//                contentType(ContentType.Application.Json)
//                setBody(GoogleTokenRequestDto(idToken = googleIdToken))
//            }
//            if (response.status.isSuccess()) { // Check for success status codes (2xx)
//                Result.success(response.body<AuthResponse>())
//            } else {
//                // Create a specific exception or return a generic one
//                Result.failure(Exception("Backend validation failed with status: ${response.status.value}"))
//            }
//        } catch (e: Exception) {
//            // Log the exception here using a proper logger
//            Result.failure(e) // Propagate the exception
//        }
//    }
//}