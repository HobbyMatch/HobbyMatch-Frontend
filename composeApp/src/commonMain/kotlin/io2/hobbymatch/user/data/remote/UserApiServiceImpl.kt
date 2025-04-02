package io2.hobbymatch.user.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io2.hobbymatch.user.data.remote.dto.UserProfileDto

class UserApiServiceImpl(private val httpClient: HttpClient) : UserApiService {
    // Przykładowy bazowy URL - przenieś do konfiguracji
    private val BASE_URL = "http://twoj_backend.com/api/users"

    override suspend fun updateUserProfile(userId: String, profile: UserProfileDto) {
        try {
            // Przykładowy request PUT - dostosuj metodę, URL i body
            httpClient.put("$BASE_URL/$userId/profile") {
                contentType(ContentType.Application.Json)
                setBody(profile)
            }
            // Tutaj można dodać obsługę odpowiedzi, jeśli API coś zwraca
        } catch (e: Exception) {
            // TODO: Lepsza obsługa błędów sieciowych/API
            println("Error updating user profile: ${e.message}")
            throw e // Rzuć dalej lub zwróć Result.failure
        }
    }
}