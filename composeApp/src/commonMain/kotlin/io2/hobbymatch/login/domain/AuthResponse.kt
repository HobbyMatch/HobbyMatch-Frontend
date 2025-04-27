package io2.hobbymatch.login.domain

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
)