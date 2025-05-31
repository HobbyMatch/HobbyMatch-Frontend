package io2.hobbymatch.events.data.remote.dtos.requests

import io2.hobbymatch.events.domain.Location
import io2.hobbymatch.hobby.domain.Hobby

data class CreateOrUpdateEventDTO(
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val location: Location,
    val price: Double,
    val minUsers: Int,
    val maxUsers: Int,
    val hobbies: List<Hobby>,
)