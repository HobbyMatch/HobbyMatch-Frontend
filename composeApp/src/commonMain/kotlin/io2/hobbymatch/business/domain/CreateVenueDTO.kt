package io2.hobbymatch.business.domain

import io2.hobbymatch.activity.domain.Activity
import io2.hobbymatch.activity.domain.Location
import kotlinx.serialization.Serializable

@Serializable
data class CreateVenueDTO(
    val location: Location,
    val hostedActivities: List<Activity> = emptyList(),
    val owner: Long
)