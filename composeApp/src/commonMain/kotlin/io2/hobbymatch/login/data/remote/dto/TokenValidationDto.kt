package io2.hobbymatch.login.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenValidationRequest(
    val idToken: String // Token uzyskany z Google Sign-In
)

@Serializable
data class TokenValidationResponse(
    val success: Boolean,
    val userId: String?, // Opcjonalnie, ID użytkownika z backendu
    val message: String? // Opcjonalnie, komunikat błędu
    // Dodaj inne dane zwracane przez backend po walidacji
)