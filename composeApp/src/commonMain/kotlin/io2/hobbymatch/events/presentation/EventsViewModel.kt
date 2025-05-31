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
    val errorMessage: String? = null
)

sealed class EventsUiEvent {
    data object LoadEvents : EventsUiEvent()
    // Możesz dodać inne zdarzenia, jeśli są potrzebne
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
        }
    }
}
