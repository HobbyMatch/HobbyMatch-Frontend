package io2.hobbymatch.auth.domain

import io2.hobbymatch.events.domain.Hobby
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val id: String,
    val name: String,
    val email: String,
    val hobbies: List<Hobby> = emptyList()
)
