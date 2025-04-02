package io2.hobbymatch.login.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io2.hobbymatch.login.data.remote.dto.TokenValidationRequest
import io2.hobbymatch.login.data.remote.dto.TokenValidationResponse

class LoginApiServiceImpl(private val httpClient: HttpClient) : LoginApiService {
    // Przykładowy URL - przenieś do konfiguracji
    private val BASE_URL = "http://twoj_backend.com/api/auth"

    override suspend fun validateGoogleToken(request: TokenValidationRequest): TokenValidationResponse {
        try {
            // Przykładowy request POST - dostosuj
            return httpClient.post("$BASE_URL/google/validate") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body() // Odbierz zdeserializowaną odpowiedź
        } catch (e: Exception) {
            // TODO: Lepsza obsługa błędów
            println("Error validating token: ${e.message}")
            // Zwróć odpowiedź o błędzie lub rzuć wyjątek
            return TokenValidationResponse(success = false, userId = null, message = e.message)
        }
    }
}