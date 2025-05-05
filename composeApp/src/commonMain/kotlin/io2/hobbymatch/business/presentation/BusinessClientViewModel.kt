package io2.hobbymatch.business.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.auth.data.AuthRepository
import io2.hobbymatch.business.data.BusinessClientRepository
import io2.hobbymatch.business.domain.BusinessClient
import io2.hobbymatch.business.domain.Venue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    data object Logout : BusinessClientUiEvent()
    data object Save : BusinessClientUiEvent()
}

class BusinessClientViewModel(
    private val repository: BusinessClientRepository,
    private val authRepository: AuthRepository
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
            is BusinessClientUiEvent.Save -> {
                saveBusinessClientProfile()
            }
            is BusinessClientUiEvent.Logout -> {
                screenModelScope.launch {
                    authRepository.logout()
                    _state.update { it.copy(isLoggedIn = false) }
                }
            }
            else -> Unit
        }
    }

    private fun loadBusinessClientProfile() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Pobierz ID użytkownika z AuthRepository
                val clientId = authRepository.loadAuthResponse()?.loginInfo?.id?.toString()
                    ?: throw IllegalStateException("Nie znaleziono ID użytkownika biznesowego")

                // Pobierz dane użytkownika biznesowego z BusinessClientRepository
                val client = repository.getBusinessClient(clientId)

                // Zaktualizuj stan UI
                _state.update {
                    it.copy(
                        name = client.name,
                        email = client.email,
                        venues = client.venues,
                        isLoading = false,
                        isError = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isError = true,
                        errorMessage = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun saveBusinessClientProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val updatedClient = BusinessClient(
                    id = 1.toString(), // Przykładowe ID
                    name = _state.value.name,
                    email = _state.value.email
                )
                repository.updateBusinessClient("1", updatedClient)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isError = true,
                        errorMessage = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }
}