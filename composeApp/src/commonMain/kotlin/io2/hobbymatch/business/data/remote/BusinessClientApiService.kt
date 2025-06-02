package io2.hobbymatch.business.data.remote

import io2.hobbymatch.business.data.remote.dtos.BusinessClientDTO
import io2.hobbymatch.business.data.remote.dtos.CreateVenueDTO
import io2.hobbymatch.business.data.remote.dtos.GetVenueDTO
import io2.hobbymatch.business.data.remote.dtos.UpdateClientDTO
import io2.hobbymatch.business.data.remote.dtos.UpdateVenueDTO
import io2.hobbymatch.business.data.remote.dtos.VenueDTO

interface BusinessClientApiService {
    /** BCLIENTS **/
    suspend fun getBusinessClient(clientId: String, accessToken: String): BusinessClientDTO
    suspend fun updateBusinessClient(clientId: String, updateClientDTO: UpdateClientDTO, accessToken: String): BusinessClientDTO

    /** VENUES **/
    suspend fun addVenue(venue: CreateVenueDTO, accessToken: String): VenueDTO
    suspend fun getVenue(venueId: String): GetVenueDTO
    suspend fun getAllVenues(): List<VenueDTO>
    suspend fun deleteVenue(venueId: String, accessToken: String)
    suspend fun updateVenue(venueId: String, venueDTO: UpdateVenueDTO, accessToken: String): VenueDTO
}