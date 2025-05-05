package io2.hobbymatch.business.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class AddVenueScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        var venueName by remember { mutableStateOf("") }
        var venueLocation by remember { mutableStateOf("") }

        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text("Add Venue") }
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

                Button(
                    onClick = { navigator.pop() },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Save Venue")
                }
            }
        }
    }
}