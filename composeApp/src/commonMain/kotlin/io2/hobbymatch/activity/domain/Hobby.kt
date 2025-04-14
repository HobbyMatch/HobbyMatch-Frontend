package io2.hobbymatch.activity.domain

import kotlinx.serialization.Serializable

@Serializable
data class Hobby(
    val id: Long,
    val name: String
)
