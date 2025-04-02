package io2.hobbymatch.login.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io2.hobbymatch.utils.navigation.ScaffoldingScreen
import kotlinx.coroutines.launch

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        // navigator
        val navigator = LocalNavigator.currentOrThrow

        var authReady by remember{ mutableStateOf(false)}
        var tokenId by remember { mutableStateOf<String?>(null) }
        var errorMessage by remember { mutableStateOf<String?>(null) }


        LaunchedEffect(Unit){
            GoogleAuthProvider.create(
                credentials = GoogleAuthCredentials(
                    //tu nie wiem co ma być:
                    serverId = "752456876739-gcngoh8smdobf2mh16vj75shp0e66h67.apps.googleusercontent.com"
                )
            )
            authReady = true
        }


        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /* ============== HERE MODIFY ================ */

            if(authReady) {
                Box(
                    // modifier = Modifier.fillMaxSize(), // <-- USUŃ LUB ZAKOMENTUJ TĘ LINIĘ
                    contentAlignment = Alignment.Center
                ){
                    GoogleButtonUiContainer(
                        onGoogleSignInResult = { googleUser ->
                            val token = googleUser?.idToken
                            token?.let{
                                tokenId = it
                                println("TOKEN ID: $tokenId")
                            } ?: run{
                                errorMessage = "Google Sign-In failed: Token is null."
                            }
                        }
                    ){
                        GoogleSignInButton(
                            onClick = {this.onClick()}
                        )
                    }
                }
            }

            tokenId?.let {
                // If you have a valid token, proceed with backend authentication
                Button(
                    onClick = {
                        // Call your authentication API with tokenId here
                        // For example, sending it to your backend server for validation
                       // Toast.makeText(context, "Token received: $it", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Text("Authenticate with Backend")
                }
            }

            errorMessage?.let {
                AlertDialog(
                    onDismissRequest = { errorMessage = null },
                    title = { Text("Error") },
                    text = { Text(it) },
                    confirmButton = {
                        Button(onClick = { errorMessage = null }) {
                            Text("OK")
                        }
                    }
                )
            }

            /* =========================================== */
            Button(
                onClick = {
                    //navigator.push(UserScreen())
                    navigator.push(ScaffoldingScreen())
                }
            ) {
                Text("Go to User Screen (SignIn in the future)")
            }
        }
    }

    @Composable
    fun Garbage() {
        val httpClient = HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val scope = rememberCoroutineScope()
        var loading by remember { mutableStateOf(false) }
        var dialogMessage by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        try {
                            val response: String = httpClient.get("http://192.168.0.54:8080/hello") {
                                accept(ContentType.Text.Plain)
                            }.body() // Ensure we extract the response body correctly
                            dialogMessage = response
                        } catch (e: Exception) {
                            dialogMessage = "Failed to load data"
                        } finally {
                            loading = false
                        }

                    }
                },
                enabled = !loading
            ) {
                if (loading) {
                    Text("Loading...")
                } else {
                    Text("Request Hello World")
                }
            }
        }

        // Show Dialog when response is received
        dialogMessage?.let {
            AlertDialog(
                onDismissRequest = { dialogMessage = null },
                title = { Text("Response") },
                text = { Text(it) },
                confirmButton = {
                    Button(onClick = { dialogMessage = null }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}