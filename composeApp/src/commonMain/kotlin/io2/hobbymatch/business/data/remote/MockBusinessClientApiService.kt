package io2.hobbymatch.business.data.remote

import io.ktor.client.HttpClient
import io2.hobbymatch.business.domain.Venue
import io2.hobbymatch.network.ApiConfig

class MockBusinessClientApiService(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : BusinessClientApiService {
    override suspend fun addVenue(clientId: String, venue: Venue): Venue {
        TODO("Not yet implemented")
    }
}