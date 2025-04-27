package io2.hobbymatch.user.domain

import io2.hobbymatch.activity.domain.Hobby
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val hobbies: List<Hobby> = emptyList(),
    val birthday: String? = null, // as ISO 8601 string: "YYYY-MM-DD"
    val bio: String? = null,
    val isActive: Boolean = true
)
