package io2.hobbymatch.business.domain

import io2.hobbymatch.events.domain.Event
import io2.hobbymatch.events.domain.Location
import kotlinx.serialization.Serializable

@Serializable
data class CreateVenueDTO(
    val location: Location,
    val hostedActivities: List<Event> = emptyList(),
    val owner: Long
)