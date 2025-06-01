package io2.hobbymatch.events.data

import io2.hobbymatch.events.data.remote.EventsApiService
import io2.hobbymatch.events.data.remote.dtos.requests.CreateOrUpdateEventDTO
import io2.hobbymatch.events.data.remote.dtos.responses.EventDTO
import io2.hobbymatch.events.domain.Event
import io2.hobbymatch.events.domain.Location
import io2.hobbymatch.user.domain.UserInEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EventsRepository(
    private val apiService: EventsApiService
) {
    // Przechowuje listę eventów, którą można obserwować przez UI
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    // Przechowuje aktualnie wybrany event
    private val _currentEvent = MutableStateFlow<Event?>(null)
    val currentEvent: StateFlow<Event?> = _currentEvent.asStateFlow()

    /**
     * Pobiera wszystkie eventy z API i aktualizuje lokalny stan
     */
    suspend fun fetchAllEvents(token: String? = null) : List<Event> {
        if (token == null) {
            throw IllegalArgumentException("Token must be provided to fetch events")
        }
        val eventsFromApi = apiService.getAllEvents(token)
        val mappedEvents = eventsFromApi.map { eventDTO ->
            mapEventDTOToEvent(eventDTO)
        }
        _events.update { mappedEvents }
        return mappedEvents
    }

    /**
     * Pobiera konkretny event po ID
     */
    suspend fun fetchEventById(eventId: Long) {
        val eventFromApi = apiService.getEvent(eventId)
        val mappedEvent = mapEventDTOToEvent(eventFromApi)
        _currentEvent.update { mappedEvent }
    }

    /**
     * Tworzy nowy event
     */
    suspend fun createEvent(event: Event, accessToken: String): Event {
        val eventDTO = apiService.createEvent(mapEventToCreateDTO(event), accessToken)
        val createdEvent = mapEventDTOToEvent(eventDTO)
        
        // Aktualizuje lokalną listę eventów dodając nowy event
        _events.update { currentEvents ->
            currentEvents + createdEvent
        }
        
        return createdEvent
    }

    /**
     * Aktualizuje istniejący event
     */
    suspend fun updateEvent(event: Event): Event {
        val eventDTO = apiService.updateEvent(event.id, mapEventToCreateDTO(event))
        val updatedEvent = mapEventDTOToEvent(eventDTO)
        
        // Aktualizuje lokalną listę eventów
        _events.update { currentEvents ->
            currentEvents.map { 
                if (it.id == updatedEvent.id) updatedEvent else it 
            }
        }
        
        // Aktualizuje currentEvent jeśli to ten sam event
        if (_currentEvent.value?.id == event.id) {
            _currentEvent.update { updatedEvent }
        }
        
        return updatedEvent
    }

    /**
     * Usuwa event
     */
    suspend fun deleteEvent(eventId: Long) {
        apiService.deleteEvent(eventId)
        
        // Usuwa event z lokalnej listy
        _events.update { currentEvents ->
            currentEvents.filter { it.id != eventId }
        }
        
        // Resetuje currentEvent jeśli to był on
        if (_currentEvent.value?.id == eventId) {
            _currentEvent.update { null }
        }
    }

    /**
     * Dołącza użytkownika do eventu
     */
    suspend fun joinEvent(eventId: Long): Event {
        val eventDTO = apiService.joinEvent(eventId)
        val updatedEvent = mapEventDTOToEvent(eventDTO)
        
        // Aktualizuje lokalną listę eventów
        _events.update { currentEvents ->
            currentEvents.map { 
                if (it.id == updatedEvent.id) updatedEvent else it 
            }
        }
        
        // Aktualizuje currentEvent jeśli to ten sam event
        if (_currentEvent.value?.id == eventId) {
            _currentEvent.update { updatedEvent }
        }
        
        return updatedEvent
    }

    /**
     * Wycofuje użytkownika z eventu
     */
    suspend fun leaveEvent(eventId: Long): Event {
        val eventDTO = apiService.leaveEvent(eventId)
        val updatedEvent = mapEventDTOToEvent(eventDTO)
        
        // Aktualizuje lokalną listę eventów
        _events.update { currentEvents ->
            currentEvents.map { 
                if (it.id == updatedEvent.id) updatedEvent else it 
            }
        }
        
        // Aktualizuje currentEvent jeśli to ten sam event
        if (_currentEvent.value?.id == eventId) {
            _currentEvent.update { updatedEvent }
        }
        
        return updatedEvent
    }


    // Metody pomocnicze do mapowania obiektów

    private fun mapEventDTOToEvent(eventDTO: EventDTO): Event {
        return Event(
            id = eventDTO.id,
            organizer = UserInEvent(
                id = eventDTO.organizer.id,
                name = eventDTO.organizer.name
            ),
            participants = eventDTO.participants.map { userDTO ->
                UserInEvent(
                    id = userDTO.id,
                    name = userDTO.name,
                )
            },
            title = eventDTO.title,
            description = eventDTO.description,
            location = Location(
                latitude = eventDTO.location.latitude,
                longitude = eventDTO.location.longitude,
            ),
            startTime = eventDTO.startTime,
            endTime = eventDTO.endTime,
            price = eventDTO.price,
            hobbies = eventDTO.hobbies,
            minUsers = eventDTO.minUsers,
            maxUsers = eventDTO.maxUsers
        )
    }

    private fun mapEventToCreateDTO(event: Event): CreateOrUpdateEventDTO {
        return CreateOrUpdateEventDTO(
            title = event.title,
            description = event.description,
            location = Location(
                latitude = event.location.latitude,
                longitude = event.location.longitude,
            ),
            startTime = event.startTime,
            endTime = event.endTime,
            price = event.price,
            hobbies = event.hobbies,
            minUsers = event.minUsers,
            maxUsers = event.maxUsers,
        )
    }
}
