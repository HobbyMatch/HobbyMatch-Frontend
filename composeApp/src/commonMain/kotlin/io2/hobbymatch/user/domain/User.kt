package io2.hobbymatch.user.domain

import io2.hobbymatch.hobby.domain.Hobby
import kotlinx.serialization.Serializable

@Serializable
data class User(
    var email: String,
    var hobbies: List<Hobby>,
    val id: String,
    var name: String
)

@Serializable
data class UserInEventDTO(
    val id: Long,
    val name: String,
)

fun User.toInEventDTO(): UserInEventDTO =
    UserInEventDTO(
        id = this.id.toLong(),
        name = this.name,
    )