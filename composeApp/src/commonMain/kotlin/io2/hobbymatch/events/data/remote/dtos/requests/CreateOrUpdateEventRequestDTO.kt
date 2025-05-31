package io2.hobbymatch.events.data.remote.dtos.requests

import io2.hobbymatch.events.domain.Location
import io2.hobbymatch.hobby.domain.Hobby
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrUpdateEventDTO(
    val title: String,
    val description: String? = null,
    val startTime: String,
    val endTime: String,
    val location: Location,
    val price: Double,
    val minUsers: Int,
    val maxUsers: Int,
    val hobbies: List<Hobby>,
)