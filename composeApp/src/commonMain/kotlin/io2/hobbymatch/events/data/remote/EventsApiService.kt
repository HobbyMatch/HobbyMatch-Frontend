package io2.hobbymatch.events.data.remote

import io2.hobbymatch.events.data.remote.dtos.requests.CreateOrUpdateEventDTO
import io2.hobbymatch.events.data.remote.dtos.responses.EventDTO

interface EventsApiService {
    suspend fun createEvent(event: CreateOrUpdateEventDTO, accessToken: String): EventDTO
    suspend fun updateEvent(eventId: Long, event: CreateOrUpdateEventDTO, accessToken: String): EventDTO
    suspend fun getEvent(eventId: Long): EventDTO
    suspend fun getAllEvents(accessToken: String): List<EventDTO>
    suspend fun deleteEvent(eventId: Long, accessToken: String)
    suspend fun joinEvent(eventId: Long, accessToken: String) : EventDTO
    suspend fun leaveEvent(eventId: Long, accessToken: String) : EventDTO
}
