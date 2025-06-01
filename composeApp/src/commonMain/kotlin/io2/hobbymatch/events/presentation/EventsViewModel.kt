package io2.hobbymatch.events.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.auth.data.AuthRepository
import io2.hobbymatch.events.data.EventsRepository
import io2.hobbymatch.events.domain.Event
import io2.hobbymatch.hobby.data.HobbyRepository
import io2.hobbymatch.user.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventsScreenState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val currentUserId: Long? = null,
    val showSnackbar: Boolean = false,
    val snackbarMessage: String = ""
)

sealed class EventsUiEvent {
    data object LoadEvents : EventsUiEvent()
    data class JoinEvent(val eventId: Long) : EventsUiEvent()
    data class LeaveEvent(val eventId: Long) : EventsUiEvent()
    data class DeleteEvent(val eventId: Long) : EventsUiEvent()
}

class EventsViewModel (
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
    private val eventsRepository: EventsRepository,
    private val authRepository: AuthRepository
) : ScreenModel {
    private val _state = MutableStateFlow(EventsScreenState())
    val state: StateFlow<EventsScreenState> = _state

    init {
        loadEvents()
        loadCurrentUserId()
    }

    private fun loadCurrentUserId() {
        screenModelScope.launch {
            try {
                val currentUserId = authRepository.loadAuthResponse()?.loginInfo?.id ?: throw Exception("Użytkownik nie jest zalogowany")
                _state.update { it.copy(currentUserId = currentUserId) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isError = true,
                        errorMessage = e.message ?: "Nie udało się załadować użytkownika",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadEvents() {
        screenModelScope.launch { 
            _state.update { it.copy(isLoading = true, isError = false, errorMessage = null) }
            try {
                val accessToken = authRepository.loadAccessToken()
                eventsRepository.fetchAllEvents(accessToken)
                // Obserwujemy flow z repozytorium
                eventsRepository.events.collect { eventsList ->
                    _state.update { it.copy(events = eventsList, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isError = true,
                        errorMessage = e.message ?: "Unknown error",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: EventsUiEvent) {
        when (event) {
            is EventsUiEvent.LoadEvents -> loadEvents()
            is EventsUiEvent.JoinEvent -> joinEvent(event.eventId)
            is EventsUiEvent.LeaveEvent -> leaveEvent(event.eventId)
            is EventsUiEvent.DeleteEvent -> deleteEvent(event.eventId)
        }
    }
    
    private fun joinEvent(eventId: Long) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false) }
            try {
                val accessToken = authRepository.loadAccessToken() ?: throw Exception("Użytkownik nie jest zalogowany")
                eventsRepository.joinEvent(eventId, accessToken)
                _state.update { it.copy(
                    isLoading = false,
                    showSnackbar = true,
                    snackbarMessage = "Dołączono do wydarzenia"
                ) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isError = true,
                        errorMessage = e.message ?: "Nie udało się dołączyć do wydarzenia",
                        isLoading = false,
                        showSnackbar = true,
                        snackbarMessage = "Błąd: Nie udało się dołączyć do wydarzenia"
                    )
                }
            }
        }
    }
    
    private fun leaveEvent(eventId: Long) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false) }
            try {
                val accessToken = authRepository.loadAccessToken() ?: throw Exception("Użytkownik nie jest zalogowany")
                eventsRepository.leaveEvent(eventId, accessToken)
                _state.update { it.copy(
                    isLoading = false,
                    showSnackbar = true,
                    snackbarMessage = "Zrezygnowano z uczestnictwa"
                ) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isError = true,
                        errorMessage = e.message ?: "Nie udało się opuścić wydarzenia",
                        isLoading = false,
                        showSnackbar = true,
                        snackbarMessage = "Błąd: Nie udało się opuścić wydarzenia"
                    )
                }
            }
        }
    }
    
    private fun deleteEvent(eventId: Long) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false) }
            try {
                val accessToken = authRepository.loadAccessToken() ?: throw Exception("Użytkownik nie jest zalogowany")
                eventsRepository.deleteEvent(eventId, accessToken)
                _state.update { it.copy(
                    isLoading = false,
                    showSnackbar = true,
                    snackbarMessage = "Wydarzenie zostało usunięte"
                ) }
                // Po usunięciu wydarzenia odświeżamy listę
                loadEvents()
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isError = true,
                        errorMessage = e.message ?: "Nie udało się usunąć wydarzenia",
                        isLoading = false,
                        showSnackbar = true,
                        snackbarMessage = "Błąd: Nie udało się usunąć wydarzenia"
                    )
                }
            }
        }
    }
    
    // Funkcja do ukrywania Snackbara po wyświetleniu
    fun hideSnackbar() {
        _state.update { it.copy(showSnackbar = false) }
    }
    
    fun isCurrentUserParticipant(event: Event): Boolean {
        val currentUserId = _state.value.currentUserId
        return event.participants.any { participant -> participant.id == currentUserId }
    }
    
    fun isCurrentUserOrganizer(event: Event): Boolean {
        val currentUserId = _state.value.currentUserId
        return currentUserId == event.organizer.id
    }
    
    fun getCurrentUserId(): Long? {
        return  _state.value.currentUserId
    }
}
