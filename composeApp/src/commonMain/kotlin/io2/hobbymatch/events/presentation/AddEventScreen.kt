package io2.hobbymatch.events.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import io2.hobbymatch.hobby.domain.Hobby
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AddEventScreen : Screen, Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.AutoMirrored.Filled.FormatListBulleted)

            return remember {
                TabOptions(
                    index = 0u,
                    title = "Activity",
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AddEventViewModel>()
        val state = viewModel.state.collectAsState().value
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        
        var showStartDatePicker by remember { mutableStateOf(false) }
        var showEndDatePicker by remember { mutableStateOf(false) }
        
        LaunchedEffect(state.eventCreated) {
            if (state.eventCreated) {
                navigator.pop()
            }
        }
        
        LaunchedEffect(state.error) {
            state.error?.let {
                scope.launch {
                    snackbarHostState.showSnackbar(it)
                }
                viewModel.onEvent(AddEventEvent.ClearError)
            }
        }
        
        if (state.isHobbySelectionOpen) {
            HobbySelectionDialog(
                availableHobbies = state.availableHobbies,
                selectedHobbies = state.selectedHobbies,
                isLoading = state.isLoadingHobbies,
                onHobbyToggle = { viewModel.onEvent(AddEventEvent.ToggleHobbySelection(it)) },
                onDismiss = { viewModel.onEvent(AddEventEvent.ToggleHobbySelectionDialog) },
                onConfirm = { viewModel.onEvent(AddEventEvent.ToggleHobbySelectionDialog) }
            )
        }
        
        if (showStartDatePicker) {
            DateTimePickerDialog(
                initialDate = state.startTime,
                onDateSelected = { viewModel.onEvent(AddEventEvent.StartTimeChanged(it)) },
                onDismiss = { showStartDatePicker = false }
            )
        }
        
        if (showEndDatePicker) {
            DateTimePickerDialog(
                initialDate = state.endTime,
                onDateSelected = { viewModel.onEvent(AddEventEvent.EndTimeChanged(it)) },
                onDismiss = { showEndDatePicker = false }
            )
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dodaj nowe wydarzenie") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Tytuł
                        OutlinedTextField(
                            value = state.title,
                            onValueChange = { viewModel.onEvent(AddEventEvent.TitleChanged(it)) },
                            label = { Text("Tytuł wydarzenia*") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.titleError != null,
                            supportingText = { state.titleError?.let { Text(it) } }
                        )
                        
                        // Opis
                        OutlinedTextField(
                            value = state.description,
                            onValueChange = { viewModel.onEvent(AddEventEvent.DescriptionChanged(it)) },
                            label = { Text("Opis wydarzenia*") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            isError = state.descriptionError != null,
                            supportingText = { state.descriptionError?.let { Text(it) } }
                        )
                        
                        // Data i czas rozpoczęcia
                        Column {
                            Text(
                                text = "Data i czas rozpoczęcia*",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showStartDatePicker = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "Select date",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    
                                    Text(
                                        text = if (state.startTime.isBlank()) 
                                                  "Kliknij, aby wybrać datę i czas rozpoczęcia" 
                                               else 
                                                  formatDateTimeForDisplay(state.startTime)
                                    )
                                }
                            }
                            
                            state.startTimeError?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                        
                        // Data i czas zakończenia
                        Column {
                            Text(
                                text = "Data i czas zakończenia*",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showEndDatePicker = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "Select date",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    
                                    Text(
                                        text = if (state.endTime.isBlank()) 
                                                  "Kliknij, aby wybrać datę i czas zakończenia" 
                                               else 
                                                  formatDateTimeForDisplay(state.endTime)
                                    )
                                }
                            }
                            
                            state.endTimeError?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                        
                        // Lokalizacja
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Lokalizacja*",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                
                                OutlinedTextField(
                                    value = state.latitude,
                                    onValueChange = { viewModel.onEvent(AddEventEvent.LatitudeChanged(it)) },
                                    label = { Text("Szerokość geograficzna") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    isError = state.locationError != null,
                                    supportingText = { Text("Użyj kropki lub przecinka jako separatora dziesiętnego") }
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                OutlinedTextField(
                                    value = state.longitude,
                                    onValueChange = { viewModel.onEvent(AddEventEvent.LongitudeChanged(it)) },
                                    label = { Text("Długość geograficzna") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    isError = state.locationError != null,
                                    supportingText = { Text("Użyj kropki lub przecinka jako separatora dziesiętnego") }
                                )
                                
                                state.locationError?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )
                                }
                            }
                        }
                        
                        // Cena
                        OutlinedTextField(
                            value = state.price,
                            onValueChange = { viewModel.onEvent(AddEventEvent.PriceChanged(it)) },
                            label = { Text("Cena w zł*") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = state.priceError != null,
                            supportingText = { 
                                if (state.priceError != null) {
                                    Text(state.priceError)
                                } else {
                                    Text("Użyj kropki lub przecinka jako separatora dziesiętnego")
                                }
                            }
                        )
                        
                        // Liczba uczestników
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Liczba uczestników: Min: ${state.minUsers}, Max: ${state.maxUsers}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text("Minimalna liczba uczestników:")
                            Slider(
                                value = state.minUsers.toFloat(),
                                onValueChange = { viewModel.onEvent(AddEventEvent.MinUsersChanged(it.roundToInt())) },
                                valueRange = 1f..50f,
                                steps = 48
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text("Maksymalna liczba uczestników:")
                            Slider(
                                value = state.maxUsers.toFloat(),
                                onValueChange = { viewModel.onEvent(AddEventEvent.MaxUsersChanged(it.roundToInt())) },
                                valueRange = state.minUsers.toFloat()..50f,
                                steps = 48 - state.minUsers
                            )
                            
                            state.usersError?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        
                        // Wybór hobby
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Wybrane hobby*",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onEvent(AddEventEvent.ToggleHobbySelectionDialog) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (state.selectedHobbies.isEmpty()) "Kliknij, aby wybrać hobby" 
                                               else "Wybrano ${state.selectedHobbies.size} hobby"
                                    )
                                    Icon(
                                        if (state.isHobbySelectionOpen) Icons.Default.KeyboardArrowUp 
                                        else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Toggle selection"
                                    )
                                }
                            }
                            
                            if (state.selectedHobbies.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.selectedHobbies.forEach { hobby ->
                                        HobbyChip(
                                            hobby = hobby,
                                            onRemove = { viewModel.onEvent(AddEventEvent.ToggleHobbySelection(hobby)) }
                                        )
                                    }
                                }
                            }
                            
                            state.hobbiesError?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        
                        // Przycisk utworzenia wydarzenia
                        Button(
                            onClick = { viewModel.onEvent(AddEventEvent.Submit) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Utwórz wydarzenie")
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun HobbyChip(hobby: Hobby, onRemove: () -> Unit) {
        Card(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hobby.name,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.height(24.dp).width(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove hobby",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

fun formatDateTimeForDisplay(dateTime: String): String {
    if (dateTime.isBlank() || dateTime.length < 19) return dateTime
    
    val date = dateTime.substring(0, 10)
    val time = dateTime.substring(11, 19)
    
    return "$date, $time"
}

@Composable
fun HobbySelectionDialog(
    availableHobbies: List<Hobby>,
    selectedHobbies: List<Hobby>,
    isLoading: Boolean,
    onHobbyToggle: (Hobby) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wybierz hobby") },
        text = {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (availableHobbies.isEmpty()) {
                Text("Brak dostępnych hobby")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    availableHobbies.forEach { hobby ->
                        val isSelected = selectedHobbies.contains(hobby)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .toggleable(
                                    value = isSelected,
                                    role = Role.Checkbox,
                                    onValueChange = { onHobbyToggle(hobby) }
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = hobby.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Zatwierdź")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}
