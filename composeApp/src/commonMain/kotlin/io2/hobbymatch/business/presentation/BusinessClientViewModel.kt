package io2.hobbymatch.business.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import io2.hobbymatch.business.data.BusinessClientRepository
import io2.hobbymatch.business.domain.Venue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    private val repository: BusinessClientRepository
) : ScreenModel {

    private val _state = MutableStateFlow(BusinessClientScreenState())
    val state: StateFlow<BusinessClientScreenState> = _state.asStateFlow()

    init {
        loadBusinessClientProfile()
    }

    private fun loadBusinessClientProfile() {
        TODO("Not yet implemented")
    }
}