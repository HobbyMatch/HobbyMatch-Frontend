package io2.hobbymatch.login.presentation

// Remove direct Realm import if it was ever added
// import io.realm.kotlin.Realm
// Remove RealmDatabase import
// import io2.hobbymatch.user.data.local.realm.RealmDatabase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.login.data.local.realm.LoginMongoDB
import io2.hobbymatch.network.LoginApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginScreenState(
    // val email: String = "", // Maybe needed later for display?
    val savedToken: String? = null, // Store the token loaded from DB
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    // val isLoggedIn: Boolean = false // Determine loggedIn based on savedToken?
) {
    // Computed property for convenience
    val isLoggedIn: Boolean get() = savedToken != null
}

sealed class LoginUiEvent {
    // Event to trigger saving the token received from Google Sign-In
    data class SaveToken(val token: String) : LoginUiEvent()
    data class GoogleSignIn(val idToken: String) : LoginUiEvent()
    // Potentially add LoadToken event if not loading automatically in init
    // data object LoadToken : LoginUiEvent()
    // Potentially add ClearToken event for logout
    // data object ClearToken : LoginUiEvent()
}

// Inject non-nullable LoginMongoDB
class LoginViewModel(
    private val loginMongoDB: LoginMongoDB,
    private val loginApiService: LoginApiService
) : ScreenModel {

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.SaveToken -> saveToken(event.token)
            is LoginUiEvent.GoogleSignIn -> handleGoogleSignIn(event.idToken)
            // Handle other existing events
        }
    }

    private fun handleGoogleSignIn(idToken: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val result = loginApiService.authenticateWithGoogle(idToken)

                if (result.isSuccess) {
                    // Save JWT token received from backend
                    val jwtToken = result.getOrNull()?.token
                    if (jwtToken != null) {
                        loginMongoDB.saveLoginToken(jwtToken)
                        // State will update via Flow observation
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = "Received null token from server"
                            )
                        }
                    }
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Authentication failed"
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Backend authentication failed: $error"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Error during authentication: ${e.message}"
                    )
                }
            }
        }
    }

    // Remove direct Realm instance
    // private val realm: Realm = RealmDatabase.instance

    private var _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state.asStateFlow()

    init {
        // Load the token automatically when ViewModel is created
        // Or observe the flow for reactive updates
        observeToken()
        // loadTokenOneTime() // Alternative: one-time load
    }

    // Option 1: Observe Flow (Recommended)
    private fun observeToken() {
        loginMongoDB.getLoginTokenFlow()
            .onStart { _state.update { it.copy(isLoading = true) } }
            .onEach { token ->
                _state.update { it.copy(savedToken = token, isLoading = false, isError = false, errorMessage = null) }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to load token: ${e.message}") }
            }
            .launchIn(screenModelScope)
    }

    // Option 2: One-time load
    private fun loadTokenOneTime() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val token = loginMongoDB.loadLoginToken()
                _state.update { it.copy(savedToken = token, isLoading = false, isError = false, errorMessage = null) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to load token: ${e.message}") }
            }
        }
    }


//    fun onEvent(event: LoginUiEvent) {
//        when (event) {
//            is LoginUiEvent.SaveToken -> saveToken(event.token)
//            // Handle other events like ClearToken if added
//        }
//    }

    private fun saveToken(token: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) } // Indicate loading state
            try {
                loginMongoDB.saveLoginToken(token)
                // State will update automatically if observing the flow.
                // If using one-time load, manually update state:
                // _state.update { it.copy(savedToken = token, isLoading = false, isError = false, errorMessage = null) }
                _state.update { it.copy(isLoading = false) } // Turn off loading manually if not observing flow strictly for this save action

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to save token: ${e.message}") }
            }
        }
    }

    // Optional: Add a clear token function for logout
    private fun clearToken() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Assuming saveLoginToken with empty string clears it,
                // or add a specific delete method in LoginMongoDB
                loginMongoDB.saveLoginToken("") // Or implement delete
                // State will update via Flow if observing
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to clear token: ${e.message}") }
            }
        }
    }
}