package io2.hobbymatch.events.data.remote.dtos.responses

import io2.hobbymatch.events.domain.Event
import io2.hobbymatch.user.domain.UserInEventDTO
import kotlinx.serialization.Serializable

@Serializable
data class EventInfoDTO(
    val id: Long,
    val title: String,
    val organizer: UserInEventDTO,
    val startTime: String,
)

fun Event.toInfoDTO() =
    EventInfoDTO(
        id = this.id,
        title = this.title,
        organizer = UserInEventDTO(this.organizer.id, this.organizer.name),
        startTime = this.startTime.toString(),
    )
