package io2.hobbymatch.business.data

import io2.hobbymatch.business.data.remote.BusinessClientApiService
import io2.hobbymatch.business.data.remote.dtos.BusinessClientDTO
import io2.hobbymatch.business.data.remote.dtos.CreateVenueDTO
import io2.hobbymatch.business.data.remote.dtos.GetVenueDTO
import io2.hobbymatch.business.data.remote.dtos.UpdateClientDTO
import io2.hobbymatch.business.data.remote.dtos.UpdateVenueDTO
import io2.hobbymatch.business.data.remote.dtos.VenueDTO
import io2.hobbymatch.business.domain.BusinessClient
import io2.hobbymatch.business.domain.Venue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BusinessClientRepository(
    private val apiService: BusinessClientApiService
) {
    // Przechowuje dane business clienta, które można obserwować przez UI
    private val _businessClient = MutableStateFlow<BusinessClient?>(null)
    val businessClient: StateFlow<BusinessClient?> = _businessClient.asStateFlow()

    // Przechowuje listę venue, którą można obserwować przez UI
    private val _venues = MutableStateFlow<List<Venue>>(emptyList())
    val venues: StateFlow<List<Venue>> = _venues.asStateFlow()

    // Przechowuje aktualnie wybrane venue
    private val _currentVenue = MutableStateFlow<Venue?>(null)
    val currentVenue: StateFlow<Venue?> = _currentVenue.asStateFlow()

    /** BUSINESS CLIENT **/

    suspend fun getBusinessClient(clientId: String, accessToken: String): BusinessClientDTO {
        val response = apiService.getBusinessClient(clientId, accessToken)
        return response
    }

    suspend fun updateBusinessClient(
        clientId: String,
        updateClientDTO: UpdateClientDTO,
        accessToken: String
    ): BusinessClientDTO {
        val response = apiService.updateBusinessClient(clientId, updateClientDTO, accessToken)
        return response
    }

    /** VENUES **/

    suspend fun addVenue(venue: CreateVenueDTO, accessToken: String): VenueDTO {
        val response = apiService.addVenue(venue, accessToken)
        return response
    }

    suspend fun updateVenue(
        venueId: String,
        updateVenueDTO: UpdateVenueDTO,
        accessToken: String
    ): VenueDTO {
        val response = apiService.updateVenue(venueId, updateVenueDTO, accessToken)
        return response
    }

    suspend fun getVenue(venueId: String): GetVenueDTO {
        val response = apiService.getVenue(venueId)
        return response
    }

    suspend fun getAllVenues(): List<VenueDTO> {
        val venuesFromApi = apiService.getAllVenues()
        return venuesFromApi
    }

    suspend fun deleteVenue(venueId: String, accessToken: String) {
        apiService.deleteVenue(venueId, accessToken)
    }
}
