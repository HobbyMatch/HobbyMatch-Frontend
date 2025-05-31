package io2.hobbymatch.events.data.remote.dtos.responses

import io2.hobbymatch.events.domain.Location
import io2.hobbymatch.hobby.domain.Hobby
import io2.hobbymatch.user.domain.UserInEventDTO
import kotlinx.serialization.Serializable

@Serializable
data class EventDTO(
    val id: Long,
    val title: String,
    val description: String?,
    val startTime: String,
    val endTime: String,
    val location: Location,
    val price: Double,
    val minUsers: Int,
    val maxUsers: Int,
    val hobbies: List<Hobby>,
    val organizer: UserInEventDTO,
    val participants: List<UserInEventDTO>,
)