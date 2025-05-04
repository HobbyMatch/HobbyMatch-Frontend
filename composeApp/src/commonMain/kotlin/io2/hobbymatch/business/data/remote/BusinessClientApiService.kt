package io2.hobbymatch.business.data.remote

import io2.hobbymatch.business.domain.Venue

interface BusinessClientApiService {
    suspend fun addVenue(clientId: String, businessObject: Venue): Venue
}