package io2.hobbymatch.login.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenRequest(
    val idToken: String
)
