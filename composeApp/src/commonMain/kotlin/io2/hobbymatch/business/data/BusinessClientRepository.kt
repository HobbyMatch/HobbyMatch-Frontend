package io2.hobbymatch.business.data

import io2.hobbymatch.business.data.remote.BusinessClientApiService
import io2.hobbymatch.business.domain.Venue

class BusinessClientRepository(
    private val apiService: BusinessClientApiService
) {
    suspend fun addVenue(clientId: String, businessObject: Venue): Venue {
        return apiService.addVenue(clientId, businessObject)
    }
}