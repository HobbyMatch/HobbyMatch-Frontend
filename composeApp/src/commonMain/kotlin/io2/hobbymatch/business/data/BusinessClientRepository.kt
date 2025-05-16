package io2.hobbymatch.business.data

import io2.hobbymatch.business.data.remote.BusinessClientApiService
import io2.hobbymatch.business.domain.BusinessClient
import io2.hobbymatch.business.domain.CreateVenueDTO
import io2.hobbymatch.business.domain.Venue

class BusinessClientRepository(
    private val apiService: BusinessClientApiService
) {
    suspend fun addVenue(clientId: String, businessObject: CreateVenueDTO): Venue {
        return apiService.addVenue(clientId, businessObject)
    }

    suspend fun getBusinessClient(clientId: String): BusinessClient {
        return apiService.getBusinessClient(clientId)
    }

    suspend fun updateBusinessClient(clientId: String, businessClient: BusinessClient): BusinessClient {
        return apiService.updateBusinessClient(clientId, businessClient)
    }

    suspend fun getVenue(venueId: String): Venue {
        return apiService.getVenue(venueId)
    }
}