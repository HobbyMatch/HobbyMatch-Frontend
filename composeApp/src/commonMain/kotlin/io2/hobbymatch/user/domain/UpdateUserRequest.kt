package io2.hobbymatch.user.domain

import io2.hobbymatch.hobby.domain.Hobby
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val name: String,
    val email: String,
    val hobbies: List<Hobby>
)