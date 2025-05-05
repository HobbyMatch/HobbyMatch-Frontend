package io2.hobbymatch.business.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io2.hobbymatch.business.domain.BusinessClient
import io2.hobbymatch.business.domain.Venue
import io2.hobbymatch.network.ApiConfig

class BusinessClientApiServiceImpl(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : BusinessClientApiService {

    override suspend fun addVenue(clientId: String, venue: Venue): Venue {
        return httpClient.post {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.ADD_VENUE))
            contentType(ContentType.Application.Json)
            setBody(venue)
        }.body()
    }

    override suspend fun getBusinessClient(clientId: String): BusinessClient {
        return httpClient.get {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.GET_BCLIENT.replace("{clientId}", clientId)))
        }.body()
    }

    override suspend fun updateBusinessClient(clientId: String, businessClient: BusinessClient): BusinessClient {
        return httpClient.put {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.UPDATE_BCLIENT.replace("{clientId}", clientId)))
            contentType(ContentType.Application.Json)
            setBody(businessClient)
        }.body()
    }

    override suspend fun getVenue(venueId: String): Venue {
        return httpClient.get {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.GET_VENUE.replace("{venueId}", venueId)))
        }.body()
    }
}