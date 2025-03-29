package io2.hobbymatch.user.presentation

// Import koinScreenModel or your DI equivalent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen

class UserScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class) // Needed for FlowRow and InputChip
    @Composable
    override fun Content() {
        // Get ViewModel instance using Koin (or your DI method)
        // Assumes UserViewModel implements ScreenModel
        val viewModel = remember { UserViewModel() } // Use remember for stability across recompositions
        val state by viewModel.state.collectAsState()

        // Temporary state for the "Add Hobby" TextField
        var newHobbyText by remember { mutableStateOf("") }
        val scrollState = rememberScrollState()
        val keyboardController = LocalSoftwareKeyboardController.current // <-- Get the controller

        Scaffold( // Optional: Provides basic layout structure
            topBar = {
                TopAppBar(title = { Text("User Profile") }) // Example TopAppBar
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply padding from Scaffold
                    .imePadding()
                    .padding(horizontal = 16.dp) // Add horizontal padding
                    .verticalScroll(scrollState), // Make content scrollable
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- Loading Indicator ---
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
                }

                // --- Error Message ---
                if (state.isError && state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // --- Input Fields ---
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(UserUiEvent.EnterEmail(it)) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    enabled = !state.isLoading,
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.username,
                    onValueChange = { viewModel.onEvent(UserUiEvent.EnterUsername(it)) },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    enabled = !state.isLoading,
                    singleLine = true
                )

                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onEvent(UserUiEvent.EnterName(it)) },
                        label = { Text("Name") },
                        modifier = Modifier.weight(1f).padding(end = 4.dp, top = 4.dp, bottom = 4.dp),
                        enabled = !state.isLoading,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.surname,
                        onValueChange = { viewModel.onEvent(UserUiEvent.EnterSurname(it)) },
                        label = { Text("Surname") },
                        modifier = Modifier.weight(1f).padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
                        enabled = !state.isLoading,
                        singleLine = true
                    )
                }


                OutlinedTextField(
                    value = state.birthday,
                    onValueChange = { viewModel.onEvent(UserUiEvent.EnterBirthday(it)) },
                    label = { Text("Birthday (YYYY-MM-DD)") }, // Placeholder format
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    enabled = !state.isLoading,
                    singleLine = true
                    // Consider using a DatePickerDialog here for better UX
                )

                OutlinedTextField(
                    value = state.gender,
                    onValueChange = { viewModel.onEvent(UserUiEvent.EnterGender(it)) },
                    label = { Text("Gender") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    enabled = !state.isLoading,
                    singleLine = true
                    // Consider using RadioButtons or DropdownMenu
                )

                OutlinedTextField(
                    value = state.bio,
                    onValueChange = { viewModel.onEvent(UserUiEvent.EnterBio(it)) },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp).padding(vertical = 4.dp),
                    enabled = !state.isLoading,
                )

                // --- Hobbies Section ---
                Spacer(modifier = Modifier.height(12.dp))
                Text("Hobbies", style = MaterialTheme.typography.titleMedium)

                // Display existing hobbies
                FlowRow( // Arranges chips that wrap to the next line
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    state.hobbies.forEach { hobby ->
                        InputChip(
                            selected = false, // Not selectable in this context
                            onClick = { /* No action needed on click */ },
                            label = { Text(hobby) },
                            enabled = !state.isLoading,
                            trailingIcon = {
                                IconButton(
                                    onClick = { viewModel.onEvent(UserUiEvent.RemoveHobby(hobby)) },
                                    modifier = Modifier.size(18.dp), // Make icon smaller
                                    enabled = !state.isLoading
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove hobby $hobby")
                                }
                            }
                        )
                    }
                }

                // Add new hobby input
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newHobbyText,
                        onValueChange = { newHobbyText = it },
                        label = { Text("Add Hobby") },
                        modifier = Modifier.weight(1f),
                        enabled = !state.isLoading,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.onEvent(UserUiEvent.AddHobby(newHobbyText))
                            newHobbyText = "" // Clear input field after adding
                        },
                        enabled = !state.isLoading && newHobbyText.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Save Button ---
                Button(
                    onClick = {
                        viewModel.onEvent(UserUiEvent.Save)
                        keyboardController?.hide() // <-- Hide keyboard
                    },
                    enabled = !state.isLoading, // Disable button while loading/saving
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Text("Save Profile")
                }
            }
        }
    }
}