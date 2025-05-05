package io2.hobbymatch.business.data.remote

import io2.hobbymatch.business.domain.BusinessClient
import io2.hobbymatch.business.domain.Venue

interface BusinessClientApiService {
    suspend fun addVenue(clientId: String, venue: Venue): Venue
    suspend fun getBusinessClient(clientId: String): BusinessClient
    suspend fun updateBusinessClient(clientId: String, businessClient: BusinessClient): BusinessClient
    suspend fun getVenue(venueId: String): Venue
}