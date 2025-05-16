package io2.hobbymatch.business.domain

import kotlinx.serialization.Serializable

@Serializable
data class BusinessClient(
    val id: String,
    val name: String,
    val email: String,
    val venues: List<Venue> = emptyList()
)
