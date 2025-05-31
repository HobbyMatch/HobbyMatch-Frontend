package io2.hobbymatch.events.data.remote

import io.ktor.client.HttpClient
import io2.hobbymatch.auth.data.AuthRepository
import io2.hobbymatch.events.data.remote.dtos.requests.CreateOrUpdateEventDTO
import io2.hobbymatch.events.data.remote.dtos.responses.EventDTO
import io2.hobbymatch.network.ApiConfig

class EventsApiServiceImpl(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig,
    private val authRepository: AuthRepository
) : EventsApiService {
    override suspend fun createEvent(event: CreateOrUpdateEventDTO): EventDTO {
        TODO("Not yet implemented")
    }

    override suspend fun updateEvent(eventId: Long, event: CreateOrUpdateEventDTO): EventDTO {
        TODO("Not yet implemented")
    }

    override suspend fun getEvent(eventId: Long): EventDTO {
        TODO("Not yet implemented")
    }

    override suspend fun getAllEvents(): List<EventDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(eventId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun joinEvent(eventId: Long): EventDTO {
        TODO("Not yet implemented")
    }

    override suspend fun leaveEvent(eventId: Long): EventDTO {
        TODO("Not yet implemented")
    }
}