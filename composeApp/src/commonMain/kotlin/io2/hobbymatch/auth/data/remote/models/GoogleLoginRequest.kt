package io2.hobbymatch.auth.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class GoogleLoginRequest(
    val idToken: String
)
