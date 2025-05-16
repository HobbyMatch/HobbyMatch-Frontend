package io2.hobbymatch.business.data.remote

import io2.hobbymatch.activity.domain.Location
import io2.hobbymatch.business.domain.BusinessClient
import io2.hobbymatch.business.domain.CreateVenueDTO
import io2.hobbymatch.business.domain.Venue

class MockBusinessClientApiService : BusinessClientApiService {
    private val mockVenues = mutableListOf<Venue>()
    private val mockClients = mutableListOf(
        BusinessClient(
            id = "1",
            name = "Business Client 1",
            email = "client1@example.com",
            venues = emptyList()
        ),
        BusinessClient(
            id = "2",
            name = "Business Client 2",
            email = "client2@example.com",
            venues = emptyList()
        ),
        BusinessClient(
            id = "3",
            name = "Business Client 3",
            email = "client3@example.com",
            venues = emptyList()
        )
    )

    init {
        // Przykładowe lokalizacje i aktywności
        val location1 = Location(latitude = 52.2297, longitude = 21.0122)
        val location2 = Location(latitude = 50.0647, longitude = 19.9450)

        // Przykładowe obiekty Venue
        val venue1 = Venue(
            id = 1,
            location = location1,
            hostedActivities = emptyList(),
            ownerId = mockClients[0]
        )
        val venue2 = Venue(
            id = 2,
            location = location2,
            hostedActivities = emptyList(),
            ownerId = mockClients[1]
        )

        // Dodanie Venue do listy i przypisanie do klientów
        mockVenues.addAll(listOf(venue1, venue2))
        mockClients[0] = mockClients[0].copy(venues = listOf(venue1))
        mockClients[1] = mockClients[1].copy(venues = listOf(venue2))
    }

    override suspend fun addVenue(clientId: String, venue: CreateVenueDTO): Venue {
        val owner = mockClients.find { it.id == clientId }
            ?: throw IllegalArgumentException("Business client not found")

        val newVenue = Venue(
            id = (mockVenues.maxOfOrNull { it.id } ?: 0) + 1,
            location = venue.location,
            hostedActivities = venue.hostedActivities,
            ownerId = owner
        )

        mockVenues.add(newVenue)
        val updatedVenues = owner.venues + newVenue
        val updatedOwner = owner.copy(venues = updatedVenues)
        mockClients[mockClients.indexOf(owner)] = updatedOwner

        return newVenue
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