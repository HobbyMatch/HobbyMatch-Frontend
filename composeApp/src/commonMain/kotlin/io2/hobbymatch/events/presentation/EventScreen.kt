package io2.hobbymatch.events.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import io2.hobbymatch.events.domain.Event
import io2.hobbymatch.events.domain.Location
import io2.hobbymatch.hobby.domain.Hobby
import kotlinx.coroutines.launch

class EventScreen : Screen, Tab {
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<EventsViewModel>()
        val state = viewModel.state.collectAsState().value
        val navigator = LocalNavigator.currentOrThrow
        
        var selectedEvent by remember { mutableStateOf<Event?>(null) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        
        // Obsługa wyświetlania Snackbara
        LaunchedEffect(state.showSnackbar) {
            if (state.showSnackbar) {
                scope.launch {
                    snackbarHostState.showSnackbar(state.snackbarMessage)
                }
                viewModel.hideSnackbar()
                // Jeśli pokazujemy Snackbar po akcji, wróćmy do listy wydarzeń
                selectedEvent = null
            }
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (selectedEvent == null) "Events" else "Event Details") },
                    actions = {
                        if (selectedEvent != null) {
                            IconButton(onClick = { selectedEvent = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        } else {
                            // Dodajemy przycisk odświeżania zamiast pull-to-refresh
                            IconButton(onClick = { viewModel.onEvent(EventsUiEvent.LoadEvents) }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                if (selectedEvent == null) {
                    FloatingActionButton(
                        onClick = { navigator.push(AddEventScreen()) },
                        modifier = Modifier.padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Event",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            },
            floatingActionButtonPosition = androidx.compose.material3.FabPosition.Start,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    } else if (state.isError) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.errorMessage ?: "Wystąpił nieznany błąd",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            IconButton(onClick = { viewModel.onEvent(EventsUiEvent.LoadEvents) }) {
                                Icon(
                                    Icons.Default.Refresh, 
                                    contentDescription = "Retry",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else if (selectedEvent != null) {
                        EventDetail(event = selectedEvent!!)
                    } else if (state.events.isEmpty()) {
                        Text(
                            text = "Brak Eventów",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        EventsList(
                            events = state.events,
                            onEventClick = { event ->
                                selectedEvent = event
                            }
                        )
                    }
                }
            }
        )
    }

    @Composable
    private fun EventsList(events: List<Event>, onEventClick: (Event) -> Unit) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            items(events) { event ->
                EventCard(event = event, onClick = { onEventClick(event) })
            }
        }
    }

    @Composable
    private fun EventCard(event: Event, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Date",
                        modifier = Modifier.padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formatDateTimeRange(event.startTime, event.endTime),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formatLocation(event.location),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Participants",
                        modifier = Modifier.padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${event.participants.size}/${event.maxUsers} uczestników",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    @Composable
    private fun EventDetail(event: Event) {
        val viewModel = koinScreenModel<EventsViewModel>()
        val scrollState = rememberScrollState()
        val navigator = LocalNavigator.currentOrThrow
        
        val isUserParticipant = viewModel.isCurrentUserParticipant(event)
        val isUserOrganizer = viewModel.isCurrentUserOrganizer(event)
        
        var showDeleteConfirmDialog by remember { mutableStateOf(false) }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Przycisk dołączania/opuszczania wydarzenia lub usuwania dla organizatora
            if (isUserOrganizer) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Jesteś organizatorem tego wydarzenia",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Row {
                        // Dodajemy przycisk edycji wydarzenia
                        androidx.compose.material3.Button(
                            onClick = { navigator.push(AddEventScreen()) },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edytuj wydarzenie",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text("Edytuj")
                        }

                        // Przycisk usuwania (istniejący)
                        androidx.compose.material3.Button(
                            onClick = { showDeleteConfirmDialog = true },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Usuń wydarzenie",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text("Usuń")
                        }
                    }
                }
            } else {
                if (event.participants.size >= event.maxUsers && !isUserParticipant) {
                    // Wydarzenie jest pełne
                    androidx.compose.material3.Button(
                        onClick = { },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Wydarzenie jest pełne")
                    }
                } else if (isUserParticipant) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = { viewModel.onEvent(EventsUiEvent.LeaveEvent(event.id)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Zrezygnuj z uczestnictwa")
                    }
                } else {
                    androidx.compose.material3.Button(
                        onClick = { viewModel.onEvent(EventsUiEvent.JoinEvent(event.id)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dołącz do wydarzenia")
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Informacje podstawowe",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Date",
                            modifier = Modifier.padding(end = 8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Termin:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatDateTimeRange(event.startTime, event.endTime),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.padding(end = 8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Lokalizacja:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatLocation(event.location),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Organizer",
                            modifier = Modifier.padding(end = 8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Organizator:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = event.organizer.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "Cena:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = formatPrice(event.price),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            if (event.description != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Opis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = event.description ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Uczestnicy (${event.participants.size}/${event.maxUsers})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (event.participants.isEmpty()) {
                        Text(
                            text = "Brak uczestników",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        event.participants.forEach { participant ->
                            Text(
                                text = participant.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hobby",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (event.hobbies.isEmpty()) {
                        Text(
                            text = "Brak przypisanych hobby",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            event.hobbies.take(3).forEach { hobby ->
                                HobbyChip(hobby = hobby)
                            }
                            if (event.hobbies.size > 3) {
                                Text(
                                    text = "+${event.hobbies.size - 3}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Dialog potwierdzający usunięcie wydarzenia
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Potwierdź usunięcie") },
                text = { Text("Czy na pewno chcesz usunąć to wydarzenie? Tej operacji nie można cofnąć.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.onEvent(EventsUiEvent.DeleteEvent(event.id))
                            showDeleteConfirmDialog = false
                            // Nie musimy już jawnie nawigować, ponieważ obsługujemy to przez LaunchedEffect
                        }
                    ) {
                        Text("Usuń", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Anuluj")
                    }
                }
            )
        }
    }

    @Composable
    private fun HobbyChip(hobby: Hobby) {
        Card(
            modifier = Modifier.padding(end = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = hobby.name,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }

    private fun formatDateTimeRange(startTime: String, endTime: String): String {
        return "$startTime - $endTime"
    }

    private fun formatLocation(location: Location): String {
        return "Lokalizacja: ${location.latitude}, ${location.longitude}"

    }

    private fun formatPrice(price: Double): String {
        return if (price == 0.0) "Za darmo" else "${price} zł"
    }
}
