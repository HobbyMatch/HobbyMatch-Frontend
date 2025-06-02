package io2.hobbymatch.business.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.auth.data.AuthRepository
import io2.hobbymatch.business.data.BusinessClientRepository
import io2.hobbymatch.business.data.remote.dtos.CreateVenueDTO
import io2.hobbymatch.business.data.remote.dtos.UpdateClientDTO
import io2.hobbymatch.business.data.remote.dtos.VenueDTO
import io2.hobbymatch.events.domain.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BusinessClientScreenState(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val taxId: String = "",
    val venues: List<VenueDTO> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = true // Assuming the user is logged in initially
)

sealed class BusinessClientUiEvent {
    data class EnterEmail(val email: String) : BusinessClientUiEvent()
    data class EnterName(val name: String) : BusinessClientUiEvent()
    data class EnterTaxId(val taxId: String) : BusinessClientUiEvent()
    data object Save : BusinessClientUiEvent()
    data object Logout : BusinessClientUiEvent()
    data object RefreshData : BusinessClientUiEvent()
}

class BusinessClientViewModel(
    private val businessClientRepository: BusinessClientRepository,
    private val authRepository: AuthRepository
) : ScreenModel {

    private val _state = MutableStateFlow(BusinessClientScreenState())
    val state: StateFlow<BusinessClientScreenState> = _state.asStateFlow()

    private var clientId: String? = null

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
            is BusinessClientUiEvent.EnterTaxId -> {
                _state.update { it.copy(taxId = event.taxId) }
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
            is BusinessClientUiEvent.RefreshData -> {
                loadBusinessClientProfile()
            }
        }
    }

    fun addVenue(name: String, description: String, address: String, location: Location) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val token = authRepository.loadAccessToken() ?: throw IllegalStateException("Token not found")
                
                // Stwórz obiekt CreateVenueDTO
                val createVenueDTO = CreateVenueDTO(
                    name = name,
                    description = description,
                    address = address,
                    location = location
                )

                // Wyślij żądanie dodania Venue
                val newVenueDTO = businessClientRepository.addVenue(createVenueDTO, token)
                
                // Odśwież dane
                loadBusinessClientProfile()
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

    private fun loadBusinessClientProfile() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Pobierz ID użytkownika z AuthRepository
                val userId = authRepository.loadAuthResponse()?.loginInfo?.id?.toString()
                    ?: throw IllegalStateException("User ID not found")
                clientId = userId
                
                // Pobierz token dostępu
                val token = authRepository.loadAccessToken() ?: throw IllegalStateException("Token not found")

                val accessToken = authRepository.loadAuthResponse()?.accessToken

                // Pobierz dane użytkownika biznesowego
                val clientDTO = businessClientRepository.getBusinessClient(userId, accessToken!!)
                
                // Pobierz listę venues
                val venuesDTO = businessClientRepository.getAllVenues()

                // Zaktualizuj stan UI
                _state.update {
                    it.copy(
                        id = clientDTO.id.toString(),
                        name = clientDTO.name,
                        email = clientDTO.email,
                        taxId = clientDTO.taxId,
                        venues = venuesDTO,
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
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val cId = clientId ?: throw IllegalStateException("Client ID not found")
                val token = authRepository.loadAccessToken() ?: throw IllegalStateException("Token not found")

                val accessToken = authRepository.loadAuthResponse()?.accessToken

                val updateDTO = UpdateClientDTO(
                    name = _state.value.name,
                    email = _state.value.email,
                    taxId = _state.value.taxId
                )
                
                businessClientRepository.updateBusinessClient(cId, updateDTO, accessToken!!)
                
                // Odśwież dane po zapisie
                loadBusinessClientProfile()
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
