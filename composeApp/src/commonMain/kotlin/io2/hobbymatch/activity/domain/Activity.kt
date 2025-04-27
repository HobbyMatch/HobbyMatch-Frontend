package io2.hobbymatch.activity.domain

import io2.hobbymatch.user.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class Activity(
    val id: Long,
    val organizer: User,
    val participants: List<User> = emptyList(),
    val title: String,
    val description: String? = null,
    val location: Location,
    val dateTime: String, // or Instant, or LocalDateTime depending on your JSON format
    val hobbies: List<Hobby> = emptyList()
)
