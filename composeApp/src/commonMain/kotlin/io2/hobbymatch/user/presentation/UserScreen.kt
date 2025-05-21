package io2.hobbymatch.user.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.InputChip
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

class UserScreen : Screen, Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Face)

            return remember {
                TabOptions(
                    index = 1u,
                    title = "User",
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        // Access UserViewModel via the screenModelScope or dependency injection
        val viewModel = koinScreenModel<UserViewModel>()
        val state = viewModel.state.collectAsState().value

        val scrollState = rememberScrollState()
        var newHobby by remember { mutableStateOf("") }

        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("User Profile") }
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Show error message if any
                    if (state.isError) {
                        Text(
                            text = state.errorMessage ?: "An error occurred.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Name Input Field
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onEvent(UserUiEvent.EnterName(it)) },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Input Field
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.onEvent(UserUiEvent.EnterEmail(it)) },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hobbies Section
                    Text("Hobbies", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.hobbies.forEach { hobby ->
                            InputChip(
                                selected = false,
                                onClick = {
                                    viewModel.onEvent(UserUiEvent.RemoveHobby(hobby.name))
                                },
                                label = { Text(text = hobby.name) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add New Hobby Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newHobby,
                            onValueChange = { newHobby = it },
                            label = { Text("Add Hobby") },
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (newHobby.isNotBlank()) {
                                    viewModel.onEvent(UserUiEvent.AddHobby(newHobby))
                                    newHobby = ""
                                }
                            }
                        ) {
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Changes Button
                    Button(
                        onClick = {
                            viewModel.onEvent(UserUiEvent.Save)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading // Disable button when loading
                    ) {
                        if (state.isLoading) {
                            Text("Saving...")
                        } else {
                            Text("Save Changes")
                        }
                    }

                    // Save Changes Button
                    Button(
                        onClick = {
                            viewModel.onEvent(UserUiEvent.Logout)
                            // navigator.push(AuthScreen())
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading // Disable button when loading
                    ) {
                        if (state.isLoggedIn) {
                            Text("Logout")
                        } else {
                            Text("Logged out")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Token Button and Display
                    Button(
                        onClick = {
                            viewModel.onEvent(UserUiEvent.ToggleTokenVisibility)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.isLoggedIn && !state.isLoading
                    ) {
                        Text(
                            if (state.isTokenVisible) "Ukryj token" else "Pokaż token"
                        )
                    }
                    
                    if (state.isTokenVisible && state.accessToken != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Access Token:",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Text(
                            text = state.accessToken,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }
        )
    }
}
