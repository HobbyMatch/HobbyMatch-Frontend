package io2.hobbymatch.events.domain

import io2.hobbymatch.hobby.domain.Hobby
import io2.hobbymatch.user.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Long,
    val organizer: User,
    val participants: List<User> = emptyList(),
    val title: String,
    val description: String? = null,
    val location: Location,
    val dateTime: String, // or Instant, or LocalDateTime depending on your JSON format
    val hobbies: List<Hobby> = emptyList()
)