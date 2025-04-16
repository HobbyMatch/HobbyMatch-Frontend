//package io2.hobbymatch.login.presentation
//
//import cafe.adriel.voyager.core.model.ScreenModel
//import cafe.adriel.voyager.core.model.screenModelScope
//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.plugins.HttpTimeout
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.http.ContentType
//import io.ktor.http.contentType
//import io.ktor.serialization.kotlinx.json.json
//import io2.hobbymatch.login.data.local.realm.LoginMongoDB
//import io2.hobbymatch.login.domain.AuthResponse
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.launchIn
//import kotlinx.coroutines.flow.onEach
//import kotlinx.coroutines.flow.onStart
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import kotlinx.serialization.json.Json
//
//data class LoginScreenState(
//    // val email: String = "", // Maybe needed later for display?
//    val savedToken: String? = null, // Store the token loaded from DB
//    val isLoading: Boolean = false,
//    val isError: Boolean = false,
//    val errorMessage: String? = null,
//    val isLoggedIn: Boolean = false
//)
//
//sealed class LoginUiEvent {
//    data class SaveToken(val token: String) : LoginUiEvent()
//    data class ValidateToken(val token: String) : LoginUiEvent()
//    data object HideError : LoginUiEvent()
//}
//
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

package io2.hobbymatch.login.presentation

// Importuj poprawioną data class AppJwt
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.login.data.repository.AppJwt
import io2.hobbymatch.login.domain.usecase.LogoutUseCase
import io2.hobbymatch.login.domain.usecase.ObserveAppTokenUseCase
import io2.hobbymatch.login.domain.usecase.ValidateGoogleTokenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Używamy poprawionej data class AppJwt
data class LoginScreenState(
    val appToken: AppJwt? = null, // Przechowuje JWT aplikacji
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false // Wyliczane na podstawie appToken != null
)

// Poprawione eventy
sealed class LoginUiEvent {
    // Event wysyłany po udanym zalogowaniu przez Google (zwraca Google ID Token)
    data class GoogleSignInSuccess(val googleIdToken: String) : LoginUiEvent()
    // Event do ukrycia komunikatu błędu
    data object HideError : LoginUiEvent()
    // Opcjonalny event wylogowania
    data object Logout : LoginUiEvent()
}

class LoginViewModel(
    // Wstrzykujemy UseCase'y zamiast repozytoriów czy datasources
    private val validateGoogleTokenUseCase: ValidateGoogleTokenUseCase,
    private val observeAppTokenUseCase: ObserveAppTokenUseCase,
    private val logoutUseCase: LogoutUseCase
    // Usunęliśmy LoadAppTokenUseCase, bo ObserveAppTokenUseCase załatwia sprawę
) : ScreenModel {

    private var _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state.asStateFlow()

    init {
        // Obserwuj zmiany w zapisanym tokenie aplikacji (JWT)
        observeAppToken()
    }

    private fun observeAppToken() {
        observeAppTokenUseCase()
            // Początkowo ustawiamy isLoading, gdy Flow startuje
            .onStart { _state.update { it.copy(isLoading = true) } }
            .onEach { appToken ->
                // Aktualizuj stan za każdym razem, gdy token się zmieni (lub jest null)
                _state.update {
                    it.copy(
                        isLoading = false, // Kończymy ładowanie po pierwszej emisji
                        appToken = appToken,
                        isLoggedIn = appToken != null, // Status logowania zależy od obecności tokenu
                        // Resetuj błędy przy zmianie statusu logowania
                        isError = if (it.isLoggedIn != (appToken != null)) false else it.isError,
                        errorMessage = if (it.isLoggedIn != (appToken != null)) null else it.errorMessage
                    )
                }
                println("App token observed: ${if (appToken != null) "Present" else "Null"}, isLoggedIn=${appToken != null}")
            }
            .catch { e ->
                // Błąd podczas obserwacji przepływu (np. błąd Realm)
                println("Error observing token flow: ${e.message}")
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Error observing token: ${e.message}"
                    )
                }
            }
            .launchIn(screenModelScope) // Uruchom w zakresie ScreenModel
    }

    // Główna metoda do obsługi zdarzeń z UI
    fun onEvent(event: LoginUiEvent) {
        when (event) {
            // Po sukcesie Google Sign In, walidujemy Google ID Token z backendem
            is LoginUiEvent.GoogleSignInSuccess -> handleGoogleSignIn(event.googleIdToken)
            // Ukrywamy błąd
            LoginUiEvent.HideError -> _state.update { it.copy(isError = false, errorMessage = null) }
            // Obsługujemy wylogowanie
            LoginUiEvent.Logout -> handleLogout()
        }
    }

    // Metoda wywoływana po otrzymaniu Google ID Token z UI
    private fun handleGoogleSignIn(googleIdToken: String) {
        screenModelScope.launch {
            // Pokaż wskaźnik ładowania i wyczyść poprzednie błędy
            _state.update { it.copy(isLoading = true, isError = false, errorMessage = null) }
            println("Calling ValidateGoogleTokenUseCase...")
            // Wywołaj use case do walidacji tokenu Google i zapisania tokenu aplikacji
            validateGoogleTokenUseCase(googleIdToken)
                .onSuccess {
                    // Sukces! Token aplikacji został zapisany przez UseCase/Repository.
                    // Stan `isLoggedIn` zaktualizuje się automatycznie dzięki `observeAppToken`.
                    // Możemy tylko wyłączyć wskaźnik ładowania.
                    _state.update { it.copy(isLoading = false) }
                    println("Google Token validation successful, App JWT saved.")
                }
                .onFailure { e ->
                    // Błąd podczas walidacji z backendem lub zapisu tokenu aplikacji
                    println("Google Token validation failed: ${e.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            // Tutaj można dać bardziej przyjazny komunikat
                            errorMessage = "Login failed: ${e.message}"
                        )
                    }
                }
        }
    }

    // Metoda do obsługi wylogowania
    private fun handleLogout() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) } // Pokaż ładowanie
            try {
                logoutUseCase() // Wywołaj use case czyszczący token
                // Stan `isLoggedIn` zaktualizuje się przez `observeAppToken` po wyczyszczeniu tokenu
                // Można od razu wyłączyć ładowanie, bo czyszczenie powinno być szybkie
                _state.update { it.copy(isLoading = false) }
                println("Logout successful.")
            } catch (e: Exception) {
                println("Logout failed: ${e.message}")
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Logout failed: ${e.message}"
                    )
                }
            }
        }
    }

    // Metoda cyklu życia ScreenModel - ważne dla Realm!
    override fun onDispose() {
        super.onDispose()
        // Tutaj potencjalnie można by zamknąć instancję Realm, jeśli jest zarządzana
        // w zakresie tego ViewModela, ale przy użyciu singletona w Koin,
        // zamykanie powinno być zarządzane globalnie lub przez samą bibliotekę Realm.
        println("LoginViewModel disposed.")
    }
}