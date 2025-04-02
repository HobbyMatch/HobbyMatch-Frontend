package io2.hobbymatch.user.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val email: String,
    val username: String,
    val name: String,
    val surname: String,
    val hobbies: List<String>,
    val birthday: String, // Rozważ użycie typu daty/czasu lub Long/Timestamp
    val gender: String,
    val bio: String
    // Dodaj inne pola, które są wysyłane/odbierane z API
)