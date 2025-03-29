package io2.hobbymatch.login.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch

class LoginScreen : Screen {
    @Composable
    override fun Content() {
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