package io2.hobbymatch.business.domain

import io2.hobbymatch.events.domain.Activity
import io2.hobbymatch.events.domain.Location
import kotlinx.serialization.Serializable

@Serializable
data class Venue(
    val id: Long,
    val location: Location,
    val hostedActivities: List<Activity>,
    val ownerId: BusinessClient
)