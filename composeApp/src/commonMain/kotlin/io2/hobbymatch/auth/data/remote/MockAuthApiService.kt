package io2.hobbymatch.auth.data.remote

    import io2.hobbymatch.activity.domain.Hobby
    import io2.hobbymatch.auth.data.remote.models.GoogleLoginRequest
    import io2.hobbymatch.auth.data.remote.models.RefreshTokenRequest
    import io2.hobbymatch.auth.domain.AuthResponse
    import io2.hobbymatch.auth.domain.LoginDTO
    import io2.hobbymatch.auth.domain.UserInfo
    import kotlinx.coroutines.delay

    class MockAuthApiService : AuthApiService {
        private val mockDelay = 500L // Symulacja opóźnienia sieciowego

        private val mockLoginInfo = LoginDTO(
            id = 1L,
            email = "jan.kowalski@example.com",
            name = "Jan Kowalski"
        )

        private val mockTokens = AuthResponse(
            accessToken = "mock_access_token",
            refreshToken = "mock_refresh_token",
            loginInfo = mockLoginInfo
        )

        override suspend fun validateGoogleIdToken(token: String, role: String): AuthResponse {
            delay(mockDelay)
            if (token == mockTokens.accessToken) {
                return mockTokens
            } else {
                throw IllegalArgumentException("Invalid token")
            }
        }

        override suspend fun refreshToken(request: RefreshTokenRequest): AuthResponse {
            delay(mockDelay)
            return mockTokens.copy(
                accessToken = "new_mock_access_token",
                refreshToken = "new_mock_refresh_token"
            )
        }

        override suspend fun googleLogin(request: GoogleLoginRequest): AuthResponse {
            delay(mockDelay)
            return mockTokens
        }

        override suspend fun getUserInfo(): UserInfo {
            delay(mockDelay)
            return UserInfo(
                id = "user123",
                name = mockLoginInfo.name,
                email = mockLoginInfo.email,
                hobbies = listOf(
                    Hobby("Photography"),
                    Hobby("Hiking"),
                    Hobby("Chess")
                )
            )
        }
    }