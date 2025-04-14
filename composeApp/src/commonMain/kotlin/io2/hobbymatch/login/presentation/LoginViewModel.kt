package io2.hobbymatch.login.presentation

// Remove direct Realm import if it was ever added
// import io.realm.kotlin.Realm
// Remove RealmDatabase import
// import io2.hobbymatch.user.data.local.realm.RealmDatabase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io2.hobbymatch.login.data.local.realm.LoginMongoDB
import io2.hobbymatch.login.domain.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

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
}

// Inject non-nullable LoginMongoDB
class LoginViewModel(private val loginMongoDB: LoginMongoDB) : ScreenModel {

    private val baseUrl = "http://192.168.0.137:8080"
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private var _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state.asStateFlow()

    init {
        //observeToken()
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


    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.SaveToken -> saveToken(event.token)
            is LoginUiEvent.ValidateToken -> validateToken(event.token)
        }
    }

    private fun validateToken(token: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val body = "{ \"idToken\": \"$token\" }"

                println("Token: $token")

                val response = httpClient.post("$baseUrl/auth/mobile/google") {
                    contentType(ContentType.Application.Json)
                    //setBody(mapOf("idToken" to token))
                    setBody(body)
                }

                if (response.status.value == 200) {
                    println("Validated with backend!")
                    val responseBody = response.body<AuthResponse>()
                    loginMongoDB.saveJwtToken(responseBody.token)
                    _state.update { it.copy(isLoading = false, isError = false, errorMessage = null, savedToken = token, isLoggedIn = true) }
                }
            }
            catch (e: Exception) { println(e) }
        }
    }

    private fun saveToken(token: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                loginMongoDB.saveLoginToken(token)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to save token: ${e.message}") }
            }
        }
    }

    private fun clearToken() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                loginMongoDB.saveLoginToken("")
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isError = true, errorMessage = "Failed to clear token: ${e.message}") }
            }
        }
    }
}