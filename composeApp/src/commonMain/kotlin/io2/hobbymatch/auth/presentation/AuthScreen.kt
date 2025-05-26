package io2.hobbymatch.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import io2.hobbymatch.business.presentation.BusinessClientScreen
import io2.hobbymatch.utils.navigation.ScaffoldingScreen

class AuthScreen : Screen {
    @Composable
    override fun Content() {
        val isMockMode = false // DEBUG PURPOSES ONLY

        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<AuthViewModel>()
        val state by viewModel.state.collectAsState()

        var authReady by remember { mutableStateOf(false) }
        var uiErrorMessage by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            GoogleAuthProvider.create(
                credentials = GoogleAuthCredentials(
                    serverId = "752456876739-gcngoh8smdobf2mh16vj75shp0e66h67.apps.googleusercontent.com"
                )
            )
            authReady = true
        }

        LaunchedEffect(state.isLoggedIn, state.isLoading) {
            if (state.isLoggedIn && !state.isLoading) {
                navigator.push(ScaffoldingScreen())
            }
        }

        LaunchedEffect(state.isBusinessClientLoggedIn, state.isLoading) {
            if (state.isBusinessClientLoggedIn && !state.isLoading) {
                navigator.push(BusinessClientScreen())
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to HobbyMatch!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (state.isLoading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (authReady && !state.isLoggedIn) {
                    Text(
                        text = "Sign in to continue",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Logowanie jako zwykły użytkownik
                    GoogleButtonUiContainer(
                        onGoogleSignInResult = { googleUser ->
                            val token = if (isMockMode) "mock_access_token" else googleUser?.idToken
                            if (token != null) {
                                viewModel.onEvent(AuthUiEvent.SaveToken(token))
                                viewModel.onEvent(AuthUiEvent.ValidateToken(token))
                            } else {
                                uiErrorMessage = "Google Sign-In failed: Token is null."
                            }
                        }
                    ) {
                        GoogleSignInButton(
                            onClick = { this.onClick() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Logowanie jako business client
                    Text(
                        text = "Sign in as a Business Client",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    GoogleButtonUiContainer(
                        onGoogleSignInResult = { googleUser ->
                            val token = if (isMockMode) "mock_access_token" else googleUser?.idToken
                            if (token != null) {
                                viewModel.onEvent(AuthUiEvent.SaveToken(token))
                                viewModel.onEvent(AuthUiEvent.ValidateBusinessClientToken(token))
                            } else {
                                uiErrorMessage = "Google Sign-In failed: Token is null."
                            }
                        }
                    ) {
                        GoogleSignInButton(
                            onClick = { this.onClick() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (state.isLoggedIn) {
                    Text(
                        text = "Logged in successfully!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navigator.push(ScaffoldingScreen()) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Proceed to App",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                uiErrorMessage?.let {
                    AlertDialog(
                        onDismissRequest = { uiErrorMessage = null },
                        title = { Text("Sign-In Error") },
                        text = { Text(it) },
                        confirmButton = {
                            Button(onClick = { uiErrorMessage = null }) {
                                Text("OK")
                            }
                        }
                    )
                }

                state.errorMessage?.let {
                    AlertDialog(
                        onDismissRequest = { },
                        title = { Text("Error") },
                        text = { Text(it) },
                        confirmButton = {
                            Button(onClick = { viewModel.onEvent(AuthUiEvent.HideError) }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }
}