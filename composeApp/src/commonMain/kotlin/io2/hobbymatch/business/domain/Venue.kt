package io2.hobbymatch.business.domain

import io2.hobbymatch.events.domain.Event
import io2.hobbymatch.events.domain.Location
import kotlinx.serialization.Serializable

@Serializable
data class Venue(
    val id: Long,
    val name: String,
    val description: String,
    val address: String,
    val location: Location,
    val owner: BusinessClient,
    val events: List<Event>
)