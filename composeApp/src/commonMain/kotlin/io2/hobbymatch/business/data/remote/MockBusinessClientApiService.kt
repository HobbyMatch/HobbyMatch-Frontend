package io2.hobbymatch.business.data.remote

import io2.hobbymatch.business.domain.BusinessClient
import io2.hobbymatch.business.domain.Venue

class MockBusinessClientApiService : BusinessClientApiService {
    private val mockVenues = mutableListOf<Venue>()
    private val mockClients = mutableListOf<BusinessClient>()

    override suspend fun addVenue(clientId: String, venue: Venue): Venue {
        mockVenues.add(venue)
        return venue
    }

    override suspend fun getBusinessClient(clientId: String): BusinessClient {
        return mockClients.find { it.id.toString() == clientId }
            ?: throw IllegalArgumentException("Business client not found")
    }

    override suspend fun updateBusinessClient(clientId: String, businessClient: BusinessClient): BusinessClient {
        val index = mockClients.indexOfFirst { it.id.toString() == clientId }
        if (index == -1) throw IllegalArgumentException("Business client not found")
        mockClients[index] = businessClient
        return businessClient
    }

    override suspend fun getVenue(venueId: String): Venue {
        return mockVenues.find { it.id.toString() == venueId }
            ?: throw IllegalArgumentException("Venue not found")
    }
}