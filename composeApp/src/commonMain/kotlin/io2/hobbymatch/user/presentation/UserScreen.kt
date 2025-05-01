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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import io2.hobbymatch.user.domain.Hobby
import io2.hobbymatch.user.domain.User

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
        // Placeholder: Simulate user data
        var user by remember { mutableStateOf(User("example@example.com", listOf(Hobby("Reading"), Hobby("Gaming")), "1", "John Doe")) }
        var userName by remember { mutableStateOf(user.name) }
        var userEmail by remember { mutableStateOf(user.email) }
        var userHobbies by remember { mutableStateOf(user.hobbies.map { it.name }) }
        val scrollState = rememberScrollState()

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

                    // Name Input Field
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Input Field
                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
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
                        userHobbies.forEach { hobby ->
                            InputChip(
                                selected = false,
                                onClick = { /* Handle click */ },
                                label = { Text(text = hobby) }
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // Add New Hobby Section
                    var newHobby by remember { mutableStateOf("") }
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
                                    userHobbies = userHobbies + newHobby
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
                            // Update user state with new values
                            user = user.copy(
                                name = userName,
                                email = userEmail,
                                hobbies = userHobbies.map { Hobby(it) }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        )
    }
}
