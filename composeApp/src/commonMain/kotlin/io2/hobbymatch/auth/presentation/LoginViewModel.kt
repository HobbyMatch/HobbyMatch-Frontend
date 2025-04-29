package io2.hobbymatch.auth.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginScreenState(
    // val email: String = "", // Maybe needed later for display?
    val savedToken: String? = null, // Store the token loaded from DB
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

sealed class LoginUiEvent {
    data class SaveToken(val token: String) : LoginUiEvent()
    data class ValidateToken(val token: String) : LoginUiEvent()
    data object HideError : LoginUiEvent()
}

//class LoginViewModel(private val loginMongoDB: LoginMongoDB) : ScreenModel {
//
//    private val baseUrl = "http://172.20.10.3:8080"
//    private val httpClient = HttpClient {
//        install(ContentNegotiation) {
//            json(Json {
//                ignoreUnknownKeys = true
//                isLenient = true
//            })
//        }
//        install(HttpTimeout) {
//            requestTimeoutMillis = 1000
//        }
//    }
//
//    private var _state = MutableStateFlow(LoginScreenState())
//    val state: StateFlow<LoginScreenState> = _state.asStateFlow()
//
//    init {
//        //observeToken()
//        screenModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            try {
//                val token = loginMongoDB.loadLoginToken()
//                if (!token.isNullOrBlank()) {
//                    _state.update {
//                        it.copy(
//                            savedToken = token,
//                            isLoading = false,
//                            isError = false,
//                            errorMessage = null
//                        )
//                    }
//                    validateToken(token)
//                } else {
//                    _state.update {
//                        it.copy(
//                            isLoading = false,
//                            isError = false,
//                            errorMessage = "Problems with validating token!",
//                            savedToken = null
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _state.update {
//                    it.copy(
//                        isLoading = false,
//                        isError = true,
//                        errorMessage = "Failed to load saved token: ${e.message}"
//                    )
//                }
//            }
//        }
//    }
//
//    fun hideError() {
//        _state.update { it.copy(isError = false, errorMessage = null) }
//    }
//
//    // Option 1: Observe Flow (Recommended)
//    private fun observeToken() {
//        loginMongoDB.getLoginTokenFlow()
//            .onStart { _state.update { it.copy(isLoading = true) } }
//            .onEach { token ->
//                _state.update { it.copy(savedToken = token, isLoading = false, isError = false, errorMessage = null) }
//            }
//            .catch { e ->
//                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to load token: ${e.message}") }
//            }
//            .launchIn(screenModelScope)
//    }
//
//    // Option 2: One-time load
//    private fun loadTokenOneTime() {
//        screenModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            try {
//                val token = loginMongoDB.loadLoginToken()
//                _state.update { it.copy(savedToken = token, isLoading = false, isError = false, errorMessage = null) }
//            } catch (e: Exception) {
//                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to load token: ${e.message}") }
//            }
//        }
//    }
//
//
//    fun onEvent(event: LoginUiEvent) {
//        when (event) {
//            is LoginUiEvent.SaveToken -> saveToken(event.token)
//            is LoginUiEvent.ValidateToken -> validateToken(event.token)
//            LoginUiEvent.HideError -> _state.update { it.copy(isError = false, errorMessage = null) }
//        }
//    }
//
//    private fun validateToken(token: String) {
//        screenModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            try {
//                val body = "{ \"idToken\": \"$token\" }"
//
//                println("Token: $token")
//
//                val response = httpClient.post("$baseUrl/auth/mobile/google") {
//                    contentType(ContentType.Application.Json)
//                    //setBody(mapOf("idToken" to token))
//                    setBody(body)
//                }
//
//                if (response.status.value == 200) {
//                    println("Validated with backend!")
//                    val responseBody = response.body<AuthResponse>()
//                    loginMongoDB.saveJwtToken(responseBody.token)
//                    _state.update { it.copy(isLoading = false, isError = false, errorMessage = null, savedToken = token, isLoggedIn = true) }
//                }
//            }
//            catch (e: Exception) {
//                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to validate token: ${e.message}") }
//            }
//        }
//    }
//
//    private fun saveToken(token: String) {
//        screenModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            try {
//                loginMongoDB.saveLoginToken(token)
//            } catch (e: Exception) {
//                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to save token: ${e.message}") }
//            }
//        }
//    }
//
//    private fun clearToken() {
//        screenModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            try {
//                loginMongoDB.saveLoginToken("")
//                _state.update { it.copy(isLoading = false) }
//            } catch (e: Exception) {
//                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to clear token: ${e.message}") }
//            }
//        }
//    }
//}

class LoginViewModel(private val authRepository: AuthRepository) : ScreenModel {

    private val _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state.asStateFlow()

    init {
        // Validate token on initialization
        screenModelScope.launch {
            val token = authRepository.loadToken()
            if (!token.isNullOrEmpty()) {
                onEvent(LoginUiEvent.ValidateToken(token))
            }
        }
    }

    // Handles UI events sent from LoginScreen
    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.ValidateToken -> validateToken(event.token)
            is LoginUiEvent.SaveToken -> saveToken(event.token)
            is LoginUiEvent.HideError -> hideError()
        }
    }

    // Function to validate the token
    private fun validateToken(token: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val response = authRepository.validateToken(token) // Call repository function
                _state.update {
                    it.copy(
                        savedToken = response.token,
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
                savedToken = refreshedToken.token,
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
