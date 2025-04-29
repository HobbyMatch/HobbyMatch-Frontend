package io2.hobbymatch.auth.domain

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    // TODO - add fields and discuss it with backend team
    val token: String,
)