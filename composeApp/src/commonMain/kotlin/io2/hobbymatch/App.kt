package io2.hobbymatch

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview


import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
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
                        val response: String = httpClient.get("http://10.0.2.2:8080/hello") {
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