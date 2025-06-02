package io2.hobbymatch.business.data.remote

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
import io2.hobbymatch.business.data.remote.dtos.BusinessClientDTO
import io2.hobbymatch.business.data.remote.dtos.CreateVenueDTO
import io2.hobbymatch.business.data.remote.dtos.GetVenueDTO
import io2.hobbymatch.business.data.remote.dtos.UpdateClientDTO
import io2.hobbymatch.business.data.remote.dtos.UpdateVenueDTO
import io2.hobbymatch.business.data.remote.dtos.VenueDTO
import io2.hobbymatch.network.ApiConfig

class BusinessClientApiServiceImpl(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : BusinessClientApiService {
    /** BUSINESS CLIENT **/

    override suspend fun getBusinessClient(
        clientId: String,
        accessToken: String
    ): BusinessClientDTO {
        return httpClient.get {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.GET_BCLIENT.replace("{clientId}", clientId)))
            headers.append("Authorization", "Bearer $accessToken")
        }.body()
    }

    override suspend fun updateBusinessClient(
        clientId: String,
        updateClientDTO: UpdateClientDTO,
        accessToken: String
    ): BusinessClientDTO {
        return httpClient.put {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.UPDATE_BCLIENT.replace("{clientId}", clientId)))
            contentType(ContentType.Application.Json)
            setBody(updateClientDTO)
            headers.append("Authorization", "Bearer $accessToken")
        }.body()
    }

    /** VENUES **/

    override suspend fun addVenue(venue: CreateVenueDTO, accessToken: String): VenueDTO {
        return httpClient.post {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.ADD_VENUE))
            contentType(ContentType.Application.Json)
            setBody(venue)
            headers.append("Authorization", "Bearer $accessToken")
        }.body()
    }

    override suspend fun getVenue(venueId: String): GetVenueDTO {
        return httpClient.get {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.GET_VENUE.replace("{venueId}", venueId)))
        }.body()
    }

    override suspend fun getAllVenues(): List<VenueDTO> {
        return httpClient.get {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.GET_ALL_VENUES))
        }.body()
    }

    override suspend fun deleteVenue(venueId: String, accessToken: String) {
        httpClient.delete {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.DELETE_VENUE.replace("{venueId}", venueId)))
            headers.append("Authorization", "Bearer $accessToken")
        }
    }

    override suspend fun updateVenue(
        venueId: String,
        venueDTO: UpdateVenueDTO,
        accessToken: String
    ): VenueDTO {
        return httpClient.put {
            url(apiConfig.getEndpoint(BusinessClientApiEndpoints.UPDATE_VENUE.replace("{venueId}", venueId)))
            contentType(ContentType.Application.Json)
            setBody(venueDTO)
            headers.append("Authorization", "Bearer $accessToken")
        }.body()
    }
}