package io2.hobbymatch.business.data.remote.dtos

import io2.hobbymatch.business.domain.BusinessClient

data class ClientInfoDTO(
    val id: Long,
    val name: String,
)

fun BusinessClient.toInfoDTO() =
    ClientInfoDTO(
        id = this.id.toLong(),
        name = this.name,
    )