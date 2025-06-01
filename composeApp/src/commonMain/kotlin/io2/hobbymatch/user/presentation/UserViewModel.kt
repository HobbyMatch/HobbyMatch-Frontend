package io2.hobbymatch.user.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.auth.data.AuthRepository
import io2.hobbymatch.hobby.data.HobbyRepository
import io2.hobbymatch.hobby.domain.Hobby
import io2.hobbymatch.user.data.UserRepository
import io2.hobbymatch.user.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserScreenState(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val hobbies: List<Hobby> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = true, // Assuming the user is logged in initially
    val isTokenVisible: Boolean = false,
    val accessToken: String? = null,
    val availableHobbies: List<Hobby> = emptyList()
)

sealed class UserUiEvent {
    data class EnterEmail(val email: String) : UserUiEvent()
    data class EnterName(val name: String) : UserUiEvent()
    data class AddHobby(val hobby: String) : UserUiEvent()
    data class RemoveHobby(val hobby: String) : UserUiEvent()
    data object Save : UserUiEvent()
    data object Logout : UserUiEvent()
    data object ToggleTokenVisibility : UserUiEvent()
}

class UserViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val hobbyRepository: HobbyRepository
) : ScreenModel {

    private val _state = MutableStateFlow(UserScreenState())
    val state: StateFlow<UserScreenState> = _state

    init {
        loadUserProfile()
        loadAccessToken()
        loadAvailableHobbies()
    }

    private fun loadAvailableHobbies() {
        screenModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                println("ViewModel: Rozpoczęto ładowanie dostępnych hobby")
                val hobbies = hobbyRepository.getHobbies()
                println("ViewModel: Załadowano ${hobbies.size} hobby: ${hobbies.joinToString { it.name }}")
                _state.update { it.copy(availableHobbies = hobbies, isLoading = false) }
            } catch (e: Exception) {
                println("ViewModel: Błąd podczas ładowania hobby: ${e.message}")
                handleError(e)
            }
        }
    }

    private fun loadUserProfile() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Pobierz ID użytkownika z AuthRepository
                val userId = authRepository.loadAuthResponse()?.loginInfo?.id?.toString()
                    ?: throw IllegalStateException("Nie znaleziono ID użytkownika")

                val token = authRepository.loadAccessToken()
                // Pobierz dane użytkownika z UserRepository
                val user = userRepository.getUserById(userId, token)

                // Zaktualizuj stan UI
                _state.update {
                    it.copy(
                        id = user.id,
                        email = user.email,
                        name = user.name,
                        hobbies = user.hobbies,
                        isLoading = false,
                        isError = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private fun loadAccessToken() {
        screenModelScope.launch {
            try {
                val token = authRepository.loadAccessToken()
                _state.update { it.copy(accessToken = token) }
            } catch (e: Exception) {
                handleError(e)
            }
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
                // Sprawdź, czy hobby już istnieje na liście użytkownika
                if (_state.value.hobbies.none { it.name == event.hobby }) {
                    // Znajdź hobby z dostępnej listy
                    val selectedHobby = _state.value.availableHobbies.find { it.name == event.hobby }
                    
                    // Dodaj hobby jeśli znaleziono w dostępnych hobby
                    selectedHobby?.let {
                        _state.update { currentState ->
                            currentState.copy(
                                hobbies = currentState.hobbies + it
                            )
                        }
                    }
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

            UserUiEvent.Logout -> {
                screenModelScope.launch {
                    authRepository.logout()
                    _state.update { it.copy(isLoggedIn = false, isTokenVisible = false) }
                }
            }

            UserUiEvent.ToggleTokenVisibility -> {
                _state.update { it.copy(isTokenVisible = !it.isTokenVisible) }
                println(state.value.accessToken)
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
            val token = currentState.accessToken
            try {
                val user = User(
                    id = currentState.id, // Pobranie ID użytkownika z aktualnego stanu
                    name = currentState.name,
                    email = currentState.email,
                    hobbies = currentState.hobbies
                )
                // Wywołanie updateUserById z UserRepository
                userRepository.updateUserById(currentState.id, user, token)
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
