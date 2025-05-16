package io2.hobbymatch.auth.domain

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val loginInfo: LoginDTO
)

@Serializable
data class LoginDTO(
    val id: Long,
    val email: String,
    val name: String,
)