package io2.hobbymatch.user.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var email: String,
    var hobbies: List<Hobby>,
    val id: String,
    var name: String
)