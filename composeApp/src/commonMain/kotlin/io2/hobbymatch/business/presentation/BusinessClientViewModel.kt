package io2.hobbymatch.business.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import io2.hobbymatch.business.domain.Venue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BusinessClientScreenState(
    val email: String = "",
    val name: String = "",
    val venues: List<Venue> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = true // Assuming the user is logged in initially
)

sealed class BusinessClientUiEvent {
    data class EnterEmail(val email: String) : BusinessClientUiEvent()
    data class EnterName(val name: String) : BusinessClientUiEvent()
    data class AddVenue(val hobby: String) : BusinessClientUiEvent()
    data class RemoveVenue(val hobby: String) : BusinessClientUiEvent()
    data object Save : BusinessClientUiEvent()
}

class BusinessClientViewModel(
    //private val repository: BusinessClientRepository
) : ScreenModel {

    private val _state = MutableStateFlow(BusinessClientScreenState())
    val state: StateFlow<BusinessClientScreenState> = _state.asStateFlow()

    init {
        loadBusinessClientProfile()
    }

    fun onEvent(event: BusinessClientUiEvent) {
        when (event) {
            is BusinessClientUiEvent.EnterEmail -> {
                _state.update { it.copy(email = event.email) }
            }
            is BusinessClientUiEvent.EnterName -> {
                _state.update { it.copy(name = event.name) }
            }
            is BusinessClientUiEvent.AddVenue -> {
                // Implement adding venue logic
            }
            is BusinessClientUiEvent.RemoveVenue -> {
                // Implement removing venue logic
            }
            is BusinessClientUiEvent.Save -> {
                // Implement save logic
            }
        }
    }

    private fun loadBusinessClientProfile() {

    }
}