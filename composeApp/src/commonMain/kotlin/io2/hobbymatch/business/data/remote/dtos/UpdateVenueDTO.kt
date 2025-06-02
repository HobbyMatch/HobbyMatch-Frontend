package io2.hobbymatch.business.data.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UpdateVenueDTO(
    val name: String,
    val description: String,
)