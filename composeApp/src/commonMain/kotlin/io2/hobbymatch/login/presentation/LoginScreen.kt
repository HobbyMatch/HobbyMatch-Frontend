//package io2.hobbymatch.login.presentation
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import cafe.adriel.voyager.core.screen.Screen
//import cafe.adriel.voyager.koin.koinScreenModel
//import cafe.adriel.voyager.navigator.LocalNavigator
//import cafe.adriel.voyager.navigator.currentOrThrow
//import com.mmk.kmpauth.google.GoogleAuthCredentials
//import com.mmk.kmpauth.google.GoogleAuthProvider
//import com.mmk.kmpauth.google.GoogleButtonUiContainer
//import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
//import io2.hobbymatch.utils.navigation.ScaffoldingScreen
//
//class LoginScreen : Screen {
//    @Composable
//    override fun Content() {
//        val navigator = LocalNavigator.currentOrThrow
//        val viewModel = koinScreenModel<LoginViewModel>()
//        val state by viewModel.state.collectAsState()
//
//        var authReady by remember { mutableStateOf(false) }
//        var uiErrorMessage by remember { mutableStateOf<String?>(null) } // For UI-specific errors like Google Sign-In failure
//
//        LaunchedEffect(Unit) {
//            GoogleAuthProvider.create(
//                credentials = GoogleAuthCredentials(
//                    serverId = "752456876739-gcngoh8smdobf2mh16vj75shp0e66h67.apps.googleusercontent.com"
//                )
//            )
//            authReady = true
//        }
//
//        LaunchedEffect(state.isLoggedIn, state.isLoading) {
//            if (state.isLoggedIn && !state.isLoading) {
//                println("Potwierdzono logowanie z backendem, nawigacja do ScaffoldingScreen...")
//                navigator.replace(ScaffoldingScreen())
//            }
//        }
//
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            // --- Loading Indicator ---
//            if (state.isLoading) {
//                CircularProgressIndicator()
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            // --- Google Sign-In Button ---
//            if (authReady && !state.isLoggedIn) {
//                Box(contentAlignment = Alignment.Center) {
//                    GoogleButtonUiContainer(
//                        onGoogleSignInResult = { googleUser ->
//                            val token = googleUser?.idToken
//                            if (token != null) {
//                                println("Google Sign-In Success - TOKEN ID: $token")
//                                viewModel.onEvent(LoginUiEvent.SaveToken(token))
//                                println("Validating token...")
//                                viewModel.onEvent(LoginUiEvent.ValidateToken(token))
//                            } else {
//                                uiErrorMessage = "Google Sign-In failed: Token is null."
//                                println("Google Sign-In failed: Token is null.")
//                            }
//                        }
//                    ) { // The content of the container is the button
//                        GoogleSignInButton(
//                            onClick = { this.onClick() }
//                        )
//                    }
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            // --- Authenticate/Proceed Button ---
//            if (state.isLoggedIn) {
//                Text("Logged in with token: ${state.savedToken?.take(10)}...") // Show partial token for confirmation
//                Spacer(modifier = Modifier.height(8.dp))
//                Button(
//                    onClick = {
//                        // TODO: Call your authentication API with state.savedToken here
//                        // or simply navigate if token implies logged in status
//                        println("Proceeding with token: ${state.savedToken}")
//                        navigator.push(ScaffoldingScreen()) // Navigate to main app screen
//                    }
//                ) {
//                    // Text("Authenticate with Backend")
//                    Text("Proceed to App") // Changed text
//                }
//                Spacer(modifier = Modifier.height(16.dp)) // Add space after button
//            }
//
//            // --- Manual Navigation Button (for testing/alternative flow) ---
//            // Consider removing this if Google Sign-In is the only way
//            Button(
//                onClick = { navigator.push(ScaffoldingScreen()) },
//                // Maybe disable if loading or logged in?
//                enabled = !state.isLoading
//            ) {
//                Text("DEV: Go to User Screen") // Clarified button purpose
//            }
//
//
//            // --- Error Dialogs ---
//            // Show UI-specific error
//            uiErrorMessage?.let {
//                AlertDialog(
//                    onDismissRequest = { uiErrorMessage = null },
//                    title = { Text("Sign-In Error") },
//                    text = { Text(it) },
//                    confirmButton = { Button(onClick = { uiErrorMessage = null }) { Text("OK") } }
//                )
//            }
//            // Show ViewModel/Database error
//            state.errorMessage?.let {
//                AlertDialog(
//                    onDismissRequest = { /* Maybe add ViewModel event to clear error */ },
//                    title = { Text("Error") },
//                    text = { Text(it) },
//                    confirmButton = { Button(onClick = { viewModel.onEvent(LoginUiEvent.HideError) }) { Text("OK") } }
//                )
//            }
//        }
//    }
//}

package io2.hobbymatch.login.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import io2.hobbymatch.utils.navigation.ScaffoldingScreen

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        // Wstrzyknij poprawnie zrefaktoryzowany LoginViewModel
        val viewModel = koinScreenModel<LoginViewModel>()
        // Obserwuj nowy stan LoginScreenState
        val state by viewModel.state.collectAsState()

        // Stan gotowości KMPAuth - bez zmian
        var authReady by remember { mutableStateOf(false) }
        // Stan dla błędów specyficznych dla UI (np. błąd samego KMPAuth)
        var uiErrorMessage by remember { mutableStateOf<String?>(null) }

        // Inicjalizacja KMPAuth - bez zmian
        LaunchedEffect(Unit) {
            try {
                GoogleAuthProvider.create(
                    credentials = GoogleAuthCredentials(
                        // Upewnij się, że to jest Twój WEB Client ID
                        serverId = "752456876739-gcngoh8smdobf2mh16vj75shp0e66h67.apps.googleusercontent.com"
                    )
                )
                authReady = true
            } catch (e: Exception) {
                uiErrorMessage = "Failed to initialize Google Auth: ${e.message}"
                println("Failed to initialize Google Auth: ${e.message}")
            }
        }

        // Nawigacja po udanym zalogowaniu (stan isLoggedIn pochodzi z ViewModel)
        LaunchedEffect(state.isLoggedIn) { // Wystarczy obserwować isLoggedIn
            // Sprawdź tylko czy isLoggedIn jest true. isLoading nie jest tu potrzebne,
            // bo nawigacja powinna nastąpić, gdy stan logowania się zmieni na true.
            if (state.isLoggedIn) {
                println("Stan isLoggedIn=true, nawigacja do ScaffoldingScreen...")
                // Użyj replace, aby ekran logowania zniknął ze stosu nawigacji
                navigator.replace(ScaffoldingScreen())
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Wskaźnik ładowania (stan z ViewModel) ---
            if (state.isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- Przycisk Google Sign-In ---
            // Pokaż tylko, gdy KMPAuth gotowe i użytkownik nie jest zalogowany
            if (authReady && !state.isLoggedIn && !state.isLoading) { // Dodano !state.isLoading dla pewności
                Box(contentAlignment = Alignment.Center) {
                    GoogleButtonUiContainer(
                        // Callback z wynikiem logowania Google
                        onGoogleSignInResult = { googleUser ->
                            val token = googleUser?.idToken // Google ID Token
                            if (token != null) {
                                println("Google Sign-In Success - Google ID TOKEN: Otrzymano")
                                // Wyślij NOWY event do ViewModel z otrzymanym tokenem Google
                                viewModel.onEvent(LoginUiEvent.GoogleSignInSuccess(token))
                                // Nie wywołujemy już SaveToken ani ValidateToken bezpośrednio
                            } else {
                                // Błąd podczas logowania przez Google (np. użytkownik anulował)
                                uiErrorMessage = "Google Sign-In failed or cancelled."
                                println("Google Sign-In failed: Token is null or user cancelled.")
                            }
                        }
                    ) { // Zawartość kontenera to przycisk
                        GoogleSignInButton(
                            onClick = {
                                // Wywołaj logikę logowania z kontenera KMPAuth
                                // Resetuj też błąd UI na wypadek ponownej próby
                                uiErrorMessage = null
                                this.onClick()
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- Przycisk / Info o zalogowaniu ---
            // Pokaż, gdy użytkownik jest zalogowany (na podstawie stanu ViewModel)
            if (state.isLoggedIn && !state.isLoading) { // Sprawdź też isLoading
                // Wyświetl fragment tokenu aplikacji (JWT)
                Text("Logged in! Token: ${state.appToken?.value?.take(10)}...")
                Spacer(modifier = Modifier.height(8.dp))
                // Przycisk "Proceed" nie jest już potrzebny, bo nawigacja jest automatyczna
                // Można go usunąć lub zamienić na przycisk "Wyloguj"
                Button(onClick = { viewModel.onEvent(LoginUiEvent.Logout) }) {
                    Text("Logout")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- Przycisk DEV (opcjonalny) ---
            Button(
                onClick = { navigator.push(ScaffoldingScreen()) },
                enabled = !state.isLoading // Wyłącz podczas ładowania
            ) {
                Text("DEV: Go to User Screen")
            }

            // --- Dialogi Błędów ---
            // Błąd UI (np. z KMPAuth)
            uiErrorMessage?.let { message -> // Użyj innej nazwy zmiennej
                AlertDialog(
                    onDismissRequest = { uiErrorMessage = null },
                    title = { Text("Sign-In Error") },
                    text = { Text(message) },
                    confirmButton = { Button(onClick = { uiErrorMessage = null }) { Text("OK") } }
                )
            }
            // Błąd z ViewModel (np. walidacja backendu, zapis/odczyt tokenu)
            state.errorMessage?.let { message -> // Użyj innej nazwy zmiennej
                AlertDialog(
                    onDismissRequest = { viewModel.onEvent(LoginUiEvent.HideError) }, // Ukryj błąd
                    title = { Text("Error") },
                    text = { Text(message) },
                    confirmButton = { Button(onClick = { viewModel.onEvent(LoginUiEvent.HideError) }) { Text("OK") } }
                )
            }
        }
    }
}