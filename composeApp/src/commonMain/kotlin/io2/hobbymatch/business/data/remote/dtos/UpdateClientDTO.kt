package io2.hobbymatch.business.data.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UpdateClientDTO(
    val name: String,
    val email: String,
    val taxId: String,
    // val venues: List<VenueDTO>,
)