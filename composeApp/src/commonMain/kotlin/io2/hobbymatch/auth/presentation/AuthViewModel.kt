package io2.hobbymatch.auth.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthScreenState(
    // val email: String = "", // Maybe needed later for display?
    val savedToken: String? = null, // Store the token loaded from DB
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val isBusinessClientLoggedIn: Boolean = false // Flag to check if the user is a business client
)

sealed class AuthUiEvent {
    data class SaveToken(val token: String) : AuthUiEvent()
    data class ValidateToken(val token: String) : AuthUiEvent()
    data class ValidateBusinessClientToken(val token: String) : AuthUiEvent()
    data object HideError : AuthUiEvent()
}

class AuthViewModel(private val authRepository: AuthRepository) : ScreenModel {

    private val _state = MutableStateFlow(AuthScreenState())
    val state: StateFlow<AuthScreenState> = _state.asStateFlow()

   init {
       screenModelScope.launch {
           val token = authRepository.loadAccessToken()
           val role = authRepository.loadRole() // Pobranie roli
           if (!token.isNullOrEmpty() && !role.isNullOrEmpty()) {
               if (role == "BUSINESS") {
                   onEvent(AuthUiEvent.ValidateBusinessClientToken(token))
               } else {
                   onEvent(AuthUiEvent.ValidateToken(token))
               }
           }
       }
   }

    // Handles UI events sent from LoginScreen
    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.ValidateToken -> validateToken(event.token)
            is AuthUiEvent.SaveToken -> saveToken(event.token)
            is AuthUiEvent.HideError -> hideError()
            is AuthUiEvent.ValidateBusinessClientToken -> validateBusinessClientToken(event.token)
        }
    }

    // Function to validate the token
    private fun validateBusinessClientToken(token: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val response = authRepository.validateToken(token, "BUSINESS") // Call repository function
                authRepository.saveAuthResponse(response, "BUSINESS")
                // print response
                println(response)
                _state.update {
                    it.copy(
                        savedToken = response.accessToken,
                        isBusinessClientLoggedIn = true,
                        isLoading = false,
                        isError = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                try {
                    refreshToken() // If token validation fails, attempt refreshing
                } catch (refreshError: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Token validation failed: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    // Function to validate the token
    private fun validateToken(token: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val response = authRepository.validateToken(token) // Call repository function
                authRepository.saveAuthResponse(response, "USER")
                // print response
                println(response)

                _state.update {
                    it.copy(
                        savedToken = response.accessToken,
                        isLoggedIn = true,
                        isLoading = false,
                        isError = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                try {
                    refreshToken() // If token validation fails, attempt refreshing
                } catch (refreshError: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Token validation failed: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    // Save the token to local storage
    private fun saveToken(token: String) {
        screenModelScope.launch {
            try {
                authRepository.saveToken(token)
                _state.update { it.copy(savedToken = token) }
                println("Token saved successfully: $token")
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isError = true,
                        errorMessage = "Failed to save token: ${e.message}"
                    )
                }
                println("Failed to save token: ${e.message}")
            }
        }
    }

    // Function to refresh the token
    private suspend fun refreshToken() {
        val refreshedToken = authRepository.refreshToken()
        _state.update {
            it.copy(
                savedToken = refreshedToken.accessToken,
                isLoggedIn = true,
                isLoading = false,
                isError = false,
                errorMessage = null
            )
        }
    }

    // Function to hide an error
    private fun hideError() {
        _state.update { it.copy(isError = false, errorMessage = null) }
    }
}
