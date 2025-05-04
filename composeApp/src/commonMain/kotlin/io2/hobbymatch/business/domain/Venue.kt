package io2.hobbymatch.business.domain

import kotlinx.serialization.Serializable

@Serializable
data class Venue(
    val id: String,
    val name: String,
    val location: String,
    val capacity: Int
)