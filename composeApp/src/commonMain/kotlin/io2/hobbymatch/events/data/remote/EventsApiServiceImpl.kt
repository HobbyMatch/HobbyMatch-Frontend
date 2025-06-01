package io2.hobbymatch.events.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io2.hobbymatch.events.data.remote.dtos.requests.CreateOrUpdateEventDTO
import io2.hobbymatch.events.data.remote.dtos.responses.EventDTO
import io2.hobbymatch.network.ApiConfig

class EventsApiServiceImpl(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : EventsApiService {
    override suspend fun createEvent(event: CreateOrUpdateEventDTO, accessToken: String): EventDTO {
        println("[EVENTS API]: Creating event with access token: $accessToken")
        println("[EVENTS API]: Event body: $event")

        val response = httpClient.post {
            url(apiConfig.getEndpoint(EventsApiEndpoints.CREATE_EVENT))
            contentType(ContentType.Application.Json)
            setBody(event)
            headers.append("Authorization", "Bearer $accessToken")
        }
        
        println("[EVENTS API]: Response status: ${response.status}")
        println("[EVENTS API]: Response body: ${response.body<String>()}")

        return response.body()
    }

    override suspend fun updateEvent(eventId: Long, event: CreateOrUpdateEventDTO): EventDTO {
        return httpClient.put {
            url(apiConfig.getEndpoint(EventsApiEndpoints.UPDATE_EVENT.replace("{eventId}", eventId.toString())))
            contentType(ContentType.Application.Json)
            setBody(event)
        }.body()
    }

    override suspend fun getEvent(eventId: Long): EventDTO {
        return httpClient.get {
            url(apiConfig.getEndpoint(EventsApiEndpoints.GET_EVENT.replace("{eventId}", eventId.toString())))
        }.body()
    }

    override suspend fun getAllEvents(token: String): List<EventDTO> {
        return httpClient.get {
            url(apiConfig.getEndpoint(EventsApiEndpoints.GET_ALL_EVENTS))
            headers.append("Authorization", "Bearer $token")
        }.body()
    }

    override suspend fun deleteEvent(eventId: Long) {
        httpClient.delete {
            url(apiConfig.getEndpoint(EventsApiEndpoints.DELETE_EVENT.replace("{eventId}", eventId.toString())))
        }
    }

    override suspend fun joinEvent(eventId: Long): EventDTO {
        return httpClient.post {
            url(apiConfig.getEndpoint(EventsApiEndpoints.JOIN_EVENT.replace("{eventId}", eventId.toString())))
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun leaveEvent(eventId: Long): EventDTO {
        return httpClient.post {
            url(apiConfig.getEndpoint(EventsApiEndpoints.LEAVE_EVENT.replace("{eventId}", eventId.toString())))
            contentType(ContentType.Application.Json)
        }.body()
    }
}
