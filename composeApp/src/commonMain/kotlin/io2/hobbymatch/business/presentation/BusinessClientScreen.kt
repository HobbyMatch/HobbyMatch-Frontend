package io2.hobbymatch.business.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import io2.hobbymatch.business.data.remote.dtos.VenueDTO

class BusinessClientScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<BusinessClientViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Business Client Profile") },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(BusinessClientUiEvent.RefreshData) }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(AddVenueScreen()) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Venue")
                }
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Business Information Section
            item {
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
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.taxId,
                            onValueChange = { onEvent(BusinessClientUiEvent.EnterTaxId(it)) },
                            label = { Text("Tax ID") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { onEvent(BusinessClientUiEvent.Save) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Changes")
                        }
                    }
                }
            }

            // Venues Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Your Venues",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }

            // List of venues
            if (state.venues.isEmpty()) {
                item {
                    Text(
                        text = "No venues yet. Add your first venue!",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(state.venues) { venue ->
                    VenueItem(venue = venue)
                }
            }

            // Error message
            if (state.isError) {
                item {
                    Text(
                        text = state.errorMessage ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                }
            }

            // Loading indicator
            if (state.isLoading) {
                item {
                    CircularProgressIndicator()
                }
            }

            // Logout button
            item {
                Button(
                    onClick = { onEvent(BusinessClientUiEvent.Logout) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }
        }
    }

    @Composable
    private fun VenueItem(
        venue: VenueDTO,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = venue.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = venue.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Address: ${venue.address}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row {
                    IconButton(onClick = { /* TODO: Edit venue */ }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit venue"
                        )
                    }
                    
                    IconButton(onClick = { /* TODO: Delete venue */ }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete venue",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
