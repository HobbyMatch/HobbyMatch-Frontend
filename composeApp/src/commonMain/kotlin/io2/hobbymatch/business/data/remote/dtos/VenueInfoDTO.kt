package io2.hobbymatch.business.data.remote.dtos

import io2.hobbymatch.business.domain.Venue
import io2.hobbymatch.events.domain.Location

data class VenueInfoDTO(
    val id: Long,
    val name: String,
    val description: String,
    val address: String,
    val location: Location,
)

fun Venue.toInfoDTO(): VenueInfoDTO =
    VenueInfoDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        address = this.address,
        location = this.location,
    )