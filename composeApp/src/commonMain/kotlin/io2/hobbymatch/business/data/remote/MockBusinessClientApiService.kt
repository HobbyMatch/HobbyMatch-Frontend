package io2.hobbymatch.business.data.remote

import io2.hobbymatch.business.domain.Venue

class MockBusinessClientApiService : BusinessClientApiService {
    override suspend fun addVenue(clientId: String, venue: Venue): Venue {
        TODO("Not yet implemented")
    }
}