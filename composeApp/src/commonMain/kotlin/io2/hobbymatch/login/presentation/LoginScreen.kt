package io2.hobbymatch.login.presentation

// Remove direct Ktor/coroutine scope imports if Garbage() is removed
// import io.ktor.client.HttpClient
// import io.ktor.client.call.body
// import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
// import io.ktor.client.request.accept
// import io.ktor.client.request.get
// import io.ktor.http.ContentType
// import io.ktor.serialization.kotlinx.json.json
// import kotlinx.coroutines.launch
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
import cafe.adriel.voyager.koin.getScreenModel
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
        // Get ViewModel using Koin
        val viewModel = getScreenModel<LoginViewModel>()
        // Observe state from ViewModel
        val state by viewModel.state.collectAsState()

        // Local state for Google Auth readiness and UI error messages
        var authReady by remember { mutableStateOf(false) }
        // Remove local tokenId - use state.savedToken from ViewModel
        // var tokenId by remember { mutableStateOf<String?>(null) }
        var uiErrorMessage by remember { mutableStateOf<String?>(null) } // For UI-specific errors like Google Sign-In failure

        // Initialize Google Auth Provider
        LaunchedEffect(Unit) {
            GoogleAuthProvider.create(
                credentials = GoogleAuthCredentials(
                    // Use your actual server client ID if needed for backend verification, otherwise maybe not needed here
                    serverId = "752456876739-gcngoh8smdobf2mh16vj75shp0e66h67.apps.googleusercontent.com"
//                    serverId = "156779813266-b35gajii2tipc0dnagqakjlpepvgvdkk.apps.googleusercontent.com",
                    )
            )
            authReady = true
            // Optional: Load token if not using Flow in ViewModel's init
            // viewModel.onEvent(LoginUiEvent.LoadToken)
        }

        LaunchedEffect(state.isLoggedIn, state.isLoading) {
            // Nawiguj tylko jeśli użytkownik jest zalogowany ORAZ ładowanie się zakończyło
            if (state.isLoggedIn && !state.isLoading) {
                println("Token znaleziony (isLoggedIn=true), nawigacja do ScaffoldingScreen...")
                // Użyj replace, aby usunąć LoginScreen ze stosu nawigacji
                // (użytkownik nie wróci "wstecz" do ekranu logowania)
                navigator.replace(ScaffoldingScreen())
                // Alternatywnie użyj push, jeśli chcesz umożliwić powrót:
                // navigator.push(ScaffoldingScreen())
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Loading Indicator ---
            if (state.isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- Google Sign-In Button ---
            // Show only if auth is ready AND user is not already logged in (has saved token)
            if (authReady && !state.isLoggedIn) {
                Box(contentAlignment = Alignment.Center) {
                    GoogleButtonUiContainer(
//                        onGoogleSignInResult = { googleUser ->
//                            val token = googleUser?.idToken
//                            if (token != null) {
//                                println("Google Sign-In Success - TOKEN ID: $token")
//                                // Send SaveToken event to ViewModel
//                                viewModel.onEvent(LoginUiEvent.SaveToken(token))
//                                // Optionally navigate immediately after saving token?
//                                // navigator.push(ScaffoldingScreen())
//                            } else {
//                                // Show UI error if Google Sign-In itself failed
//                                uiErrorMessage = "Google Sign-In failed: Token is null."
//                                println("Google Sign-In failed: Token is null.")
//                            }
//                        }
                        onGoogleSignInResult = { googleUser ->
                            val token = googleUser?.idToken
                            if (token != null) {
                                println("Google Sign-In Success - TOKEN ID: $token")
                                // Instead of just saving the Google token, send it to backend
                                viewModel.onEvent(LoginUiEvent.GoogleSignIn(token))
                            } else {
                                uiErrorMessage = "Google Sign-In failed: Token is null."
                                println("Google Sign-In failed: Token is null.")
                            }
                        }
                    ) { // The content of the container is the button
                        GoogleSignInButton(
                            onClick = { this.onClick() } // Trigger the container's internal logic
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Add space after button
            }

            // --- Authenticate/Proceed Button ---
            // Show if logged in (token exists in state)
            if (state.isLoggedIn) {
                Text("Logged in with token: ${state.savedToken?.take(10)}...") // Show partial token for confirmation
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        // TODO: Call your authentication API with state.savedToken here
                        // or simply navigate if token implies logged in status
                        println("Proceeding with token: ${state.savedToken}")
                        navigator.push(ScaffoldingScreen()) // Navigate to main app screen
                    }
                ) {
                    // Text("Authenticate with Backend")
                    Text("Proceed to App") // Changed text
                }
                Spacer(modifier = Modifier.height(16.dp)) // Add space after button
            }

            // --- Manual Navigation Button (for testing/alternative flow) ---
            // Consider removing this if Google Sign-In is the only way
            Button(
                onClick = { navigator.push(ScaffoldingScreen()) },
                // Maybe disable if loading or logged in?
                enabled = !state.isLoading
            ) {
                Text("DEV: Go to User Screen") // Clarified button purpose
            }


            // --- Error Dialogs ---
            // Show UI-specific error
            uiErrorMessage?.let {
                AlertDialog(
                    onDismissRequest = { uiErrorMessage = null },
                    title = { Text("Sign-In Error") },
                    text = { Text(it) },
                    confirmButton = { Button(onClick = { uiErrorMessage = null }) { Text("OK") } }
                )
            }
            // Show ViewModel/Database error
            state.errorMessage?.let {
                AlertDialog(
                    onDismissRequest = { /* Maybe add ViewModel event to clear error */ },
                    title = { Text("Error") },
                    text = { Text(it) },
                    confirmButton = { Button(onClick = { /* ViewModel event to clear error */ }) { Text("OK") } }
                )
            }
        }
    }

    // Remove the Garbage() function if no longer needed
    // @Composable
    // fun Garbage() { ... }
}