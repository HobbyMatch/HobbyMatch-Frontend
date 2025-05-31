package io2.hobbymatch.user.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserInEvent(
    val id: Long,
    val name: String,
)
