package io2.hobbymatch.business.data.remote.dtos

import io2.hobbymatch.business.domain.BusinessClient

data class BusinessClientDTO(
    val id: Long,
    val name: String,
    val email: String,
    val taxId: String,
    val venues: List<VenueInfoDTO>,
)

fun BusinessClient.toDTO(): BusinessClientDTO =
    BusinessClientDTO(
        id = this.id.toLong(),
        name = this.name,
        email = this.email,
        taxId = this.taxId,
        venues = this.venues.map { it.toInfoDTO() },
    )
