package io2.hobbymatch.business.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import io2.hobbymatch.events.domain.Location

class AddVenueScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        var venueName by remember { mutableStateOf("") }
        var venueLocation by remember { mutableStateOf("") }
        var locationError by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var hasStartedAdding by remember { mutableStateOf(false) }

        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<BusinessClientViewModel>()
        val state by viewModel.state.collectAsState()

        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text("Add Venue") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = venueName,
                    onValueChange = { venueName = it },
                    label = { Text("Venue Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = venueLocation,
                    onValueChange = { venueLocation = it },
                    label = { Text("Venue Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (locationError) {
                    Text(
                        text = "Invalid location format. Use latitude,longitude.",
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            try {
                                val (latitude, longitude) = venueLocation.split(",").map { it.trim().toDouble() }
                                val location = Location(latitude = latitude, longitude = longitude)

                                isLoading = true
                                locationError = false
                                hasStartedAdding = true

                                viewModel.addVenue(
                                    location = location,
                                    hostedActivities = emptyList()
                                )
                            } catch (e: Exception) {
                                locationError = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Text("Save Venue")
                    }
                }

                LaunchedEffect(state.isLoading, hasStartedAdding) {
                    if (hasStartedAdding && !state.isLoading) {
                        isLoading = false
                        if (!state.isError) {
                            navigator.pop()
                        } else {
                            locationError = true
                        }
                    }
                }
            }
        }
    }
}