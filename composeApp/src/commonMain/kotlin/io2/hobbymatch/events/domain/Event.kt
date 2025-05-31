package io2.hobbymatch.events.domain

import io2.hobbymatch.business.domain.Venue
import io2.hobbymatch.hobby.domain.Hobby
import io2.hobbymatch.user.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Long = 0,
    val organizer: User,
    val participants: MutableSet<User> = mutableSetOf(),
    val venue: Venue? = null,
    var title: String,
    var description: String? = null,
    var location: Location,
    var startTime: String, // używamy String jako reprezentacji LocalDateTime w serializacji
    var endTime: String,   // używamy String jako reprezentacji LocalDateTime w serializacji
    var price: Double,
    var hobbies: MutableSet<Hobby> = mutableSetOf(),
    var minUsers: Int,
    var maxUsers: Int
)
