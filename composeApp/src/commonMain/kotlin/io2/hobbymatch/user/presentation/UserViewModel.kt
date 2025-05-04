package io2.hobbymatch.user.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.user.data.UserRepository
import io2.hobbymatch.user.domain.Hobby
import io2.hobbymatch.user.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserScreenState(
    val email: String = "",
    val name: String = "",
    val hobbies: List<Hobby> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = true // Assuming the user is logged in initially
)

sealed class UserUiEvent {
    data class EnterEmail(val email: String) : UserUiEvent()
    data class EnterName(val name: String) : UserUiEvent()
    data class AddHobby(val hobby: String) : UserUiEvent()
    data class RemoveHobby(val hobby: String) : UserUiEvent()
    data object Save : UserUiEvent()
}

class UserViewModel(
    private val userRepository: UserRepository
) : ScreenModel {

    private val _state = MutableStateFlow(UserScreenState())
    val state: StateFlow<UserScreenState> = _state

    init {
        loadUserProfile()
    }

    /**
     * Observes user profile from local storage and updates the state.
     */
    private fun loadUserProfile() {
        screenModelScope.launch {
            userRepository.observeAuthenticatedUser()
                .catch { handleError(it) } // Handle any errors while observing the flow
                .onEach { user ->
                    if (user != null) {
                        _state.update { currentState ->
                            currentState.copy(
                                email = user.email,
                                name = user.name,
                                hobbies = user.hobbies,
                                isLoading = false,
                                isError = false,
                                errorMessage = null
                            )
                        }
                    }
                }
                .launchIn(screenModelScope)
        }
    }

    /**
     * Handles user events and updates accordingly.
     */
    fun onEvent(event: UserUiEvent) {
        when (event) {
            is UserUiEvent.EnterEmail -> {
                _state.update { it.copy(email = event.email) }
            }
            is UserUiEvent.EnterName -> {
                _state.update { it.copy(name = event.name) }
            }
            is UserUiEvent.AddHobby -> {
                _state.update { currentState ->
                    currentState.copy(
                        hobbies = currentState.hobbies + Hobby(event.hobby)
                    )
                }
            }
            is UserUiEvent.RemoveHobby -> {
                _state.update { currentState ->
                    currentState.copy(
                        hobbies = currentState.hobbies.filter { it.name != event.hobby }
                    )
                }
            }
            is UserUiEvent.Save -> {
                saveUserProfile()
            }
        }
    }

    /**
     * Saves the user profile by synchronizing it with the repository.
     */
    private fun saveUserProfile() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentState = _state.value
            try {
                val user = User(
                    id = "", // Assuming ID is managed internally in the repository
                    name = currentState.name,
                    email = currentState.email,
                    hobbies = currentState.hobbies
                )
                userRepository.updateAuthenticatedUser(user)
                _state.update { it.copy(isLoading = false, isError = false) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    /**
     * Handle any errors that occur.
     */
    private fun handleError(throwable: Throwable) {
        val errorMessage = throwable.message ?: "An unexpected error occurred."
        _state.update {
            it.copy(
                isLoading = false,
                isError = true,
                errorMessage = errorMessage
            )
        }
    }
}