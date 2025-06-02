package io2.hobbymatch.business.data.remote.dtos

import io2.hobbymatch.events.domain.Location
import kotlinx.serialization.Serializable

@Serializable
data class CreateVenueDTO(
    val name: String,
    val description: String,
    val address: String,
    val location: Location,
)