package io2.hobbymatch.business.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<BusinessClientViewModel>()
        val state by viewModel.state.collectAsState()

        var venueName by remember { mutableStateOf("") }
        var venueDescription by remember { mutableStateOf("") }
        var venueAddress by remember { mutableStateOf("") }
        var latitude by remember { mutableStateOf("") }
        var longitude by remember { mutableStateOf("") }
        
        var nameError by remember { mutableStateOf(false) }
        var descriptionError by remember { mutableStateOf(false) }
        var addressError by remember { mutableStateOf(false) }
        var locationError by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add New Venue") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
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
                    onValueChange = { 
                        venueName = it
                        nameError = it.isBlank()
                    },
                    label = { Text("Venue Name") },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError) {
                    Text(
                        text = "Name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = venueDescription,
                    onValueChange = { 
                        venueDescription = it
                        descriptionError = it.isBlank()
                    },
                    label = { Text("Description") },
                    isError = descriptionError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (descriptionError) {
                    Text(
                        text = "Description cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = venueAddress,
                    onValueChange = { 
                        venueAddress = it 
                        addressError = it.isBlank()
                    },
                    label = { Text("Address") },
                    isError = addressError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (addressError) {
                    Text(
                        text = "Address cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = latitude,
                    onValueChange = { 
                        latitude = it.replace(',', '.')
                        locationError = !isValidLatitude(latitude)
                    },
                    label = { Text("Latitude") },
                    isError = locationError,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = longitude,
                    onValueChange = { 
                        longitude = it.replace(',', '.')
                        locationError = !isValidLongitude(longitude)
                    },
                    label = { Text("Longitude") },
                    isError = locationError,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (locationError) {
                    Text(
                        text = "Invalid location coordinates",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    Button(
                        onClick = {
                            // Validate inputs
                            nameError = venueName.isBlank()
                            descriptionError = venueDescription.isBlank()
                            addressError = venueAddress.isBlank()
                            locationError = !isValidLatitude(latitude) || !isValidLongitude(longitude)

                            if (!nameError && !descriptionError && !addressError && !locationError) {
                                try {
                                    val latValue = latitude.toDouble()
                                    val lngValue = longitude.toDouble()
                                    
                                    viewModel.addVenue(
                                        name = venueName,
                                        description = venueDescription,
                                        address = venueAddress,
                                        location = Location(
                                            latitude = latValue,
                                            longitude = lngValue
                                        )
                                    )
                                    
                                    // Return to previous screen after successful addition
                                    if (!state.isError) {
                                        navigator.pop()
                                    }
                                } catch (e: Exception) {
                                    locationError = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Venue")
                    }
                }
            }
        }
    }
    
    private fun isValidLatitude(lat: String): Boolean {
        return try {
            val value = lat.toDouble()
            value >= -90.0 && value <= 90.0
        } catch (e: Exception) {
            false
        }
    }
    
    private fun isValidLongitude(lng: String): Boolean {
        return try {
            val value = lng.toDouble()
            value >= -180.0 && value <= 180.0
        } catch (e: Exception) {
            false
        }
    }
}
