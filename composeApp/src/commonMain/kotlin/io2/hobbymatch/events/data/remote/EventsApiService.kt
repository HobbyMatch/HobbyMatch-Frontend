package io2.hobbymatch.events.data.remote

import io2.hobbymatch.events.data.remote.dtos.requests.CreateOrUpdateEventDTO
import io2.hobbymatch.events.data.remote.dtos.responses.EventDTO

interface EventsApiService {
    suspend fun createEvent(event: CreateOrUpdateEventDTO): EventDTO
    suspend fun updateEvent(eventId: Long, event: CreateOrUpdateEventDTO): EventDTO
    suspend fun getEvent(eventId: Long): EventDTO
    suspend fun getAllEvents(): List<EventDTO>
    suspend fun deleteEvent(eventId: Long)
    suspend fun joinEvent(eventId: Long) : EventDTO
    suspend fun leaveEvent(eventId: Long) : EventDTO
}