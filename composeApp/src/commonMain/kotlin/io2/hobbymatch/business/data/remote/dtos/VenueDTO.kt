package io2.hobbymatch.business.data.remote.dtos

import io2.hobbymatch.business.domain.Venue
import io2.hobbymatch.events.domain.Location

data class VenueDTO(
    val id: Long,
    val name: String,
    val description: String,
    val address: String,
    val location: Location,
    val owner: ClientInfoDTO,
)

fun Venue.toDTO() =
    VenueDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        address = this.address,
        location = this.location,
        owner = this.owner.toInfoDTO(),
    )