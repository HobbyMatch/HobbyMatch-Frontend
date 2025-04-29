package io2.hobbymatch.auth.data.remote

import io2.hobbymatch.activity.domain.Hobby
import io2.hobbymatch.auth.data.remote.models.GoogleLoginRequest
import io2.hobbymatch.auth.data.remote.models.RefreshTokenRequest
import io2.hobbymatch.auth.domain.AuthResponse
import io2.hobbymatch.auth.domain.UserInfo
import kotlinx.coroutines.delay

class MockAuthApiService : AuthApiService {
    private val mockDelay = 500L // Simulate network delay

    override suspend fun validateIdToken(token: String): AuthResponse {
        if (token == mockTokens.token) {
            return mockTokens
        } else {
            throw IllegalArgumentException("Invalid token")
        }
    }


    private val mockUserInfo = UserInfo(
        id = "user123",
        name = "Jan Kowalski",
        email = "jan.kowalski@example.com",
        hobbies = listOf(
            Hobby("Photography"),
            Hobby("Hiking"),
            Hobby("Chess")
        )
    )

    private val mockTokens = AuthResponse(
        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwibmFtZSI6IkphbiBLb3dhbHNraSIsImlhdCI6MTY3NTEyMzQ1Nn0.7fC0d4-oJ6VQR8K8H2ZHGwXH6PJACMjwR7qVGOfte50"
    )

    override suspend fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        delay(mockDelay)
        return mockTokens.copy(
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwibmFtZSI6IkphbiBLb3dhbHNraSIsImlhdCI6MTY3NTEyMzQ1Nn0.7fC0d4-oJ6VQR8K8H2ZHGwXH6PJACMjwR7qVGOfte50"
        )
    }

    override suspend fun googleLogin(request: GoogleLoginRequest): AuthResponse {
        delay(mockDelay)
        return mockTokens
    }

    override suspend fun getUserInfo(): UserInfo {
        delay(mockDelay)
        return mockUserInfo
    }
}