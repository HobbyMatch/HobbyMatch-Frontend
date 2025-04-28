package io2.hobbymatch.auth.domain

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
)