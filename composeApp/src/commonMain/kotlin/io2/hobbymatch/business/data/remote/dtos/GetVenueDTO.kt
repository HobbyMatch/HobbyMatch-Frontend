package io2.hobbymatch.business.data.remote.dtos

import io2.hobbymatch.business.domain.Venue
import io2.hobbymatch.events.data.remote.dtos.responses.EventInfoDTO
import io2.hobbymatch.events.data.remote.dtos.responses.toInfoDTO
import io2.hobbymatch.events.domain.Location
import kotlinx.serialization.Serializable

@Serializable
data class GetVenueDTO(
    val id: Long,
    val name: String,
    val description: String,
    val address: String,
    val location: Location,
    val owner: ClientInfoDTO,
    val events: List<EventInfoDTO>,
)

fun Venue.toGetDTO() =
    GetVenueDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        address = this.address,
        location = this.location,
        owner = this.owner.toInfoDTO(),
        events = this.events.map { it.toInfoDTO() }
    )