package io2.hobbymatch.business.data.remote.dtos

import io2.hobbymatch.business.domain.BusinessClient
import kotlinx.serialization.Serializable

@Serializable
data class ClientInfoDTO(
    val id: Long,
    val name: String,
)

fun BusinessClient.toInfoDTO() =
    ClientInfoDTO(
        id = this.id.toLong(),
        name = this.name,
    )