package io2.hobbymatch.business.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io2.hobbymatch.business.domain.Venue

class BusinessClientScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<BusinessClientViewModel>()
        val state by viewModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Business Client Profile") }
                )
            }
        ) { paddingValues ->
            BusinessClientContent(
                state = state,
                onEvent = viewModel::onEvent,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

    @Composable
    private fun BusinessClientContent(
        state: BusinessClientScreenState,
        onEvent: (BusinessClientUiEvent) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Client Information Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Business Information",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { onEvent(BusinessClientUiEvent.EnterName(it)) },
                        label = { Text("Business Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { onEvent(BusinessClientUiEvent.EnterEmail(it)) },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Venues Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Venues",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Button(
                            onClick = {
                                onEvent(BusinessClientUiEvent.AddVenue(""))
                                navigator.push(AddVenueScreen())
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add venue")
                            Spacer(Modifier.width(4.dp))
                            Text("Add Venue")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.venues) { venue ->
                            VenueItem(
                                venue = venue,
                                onRemove = { onEvent(BusinessClientUiEvent.RemoveVenue(venue.id.toString())) }
                            )
                        }
                    }
                }
            }

            if (state.isError) {
                Text(
                    text = state.errorMessage ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Button(
                onClick = {
                    if (navigator.canPop) {
                        onEvent(BusinessClientUiEvent.Logout)
                        // navigator.push(AuthScreen())
                    } else {
                        println("Navigator cannot pop")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }

        }
    }

    @Composable
    private fun VenueItem(
        venue: Venue,
        onRemove: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = venue.id.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = venue.location.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove venue",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}