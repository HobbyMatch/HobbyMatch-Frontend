package io2.hobbymatch.events.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.auth.data.AuthRepository
import io2.hobbymatch.events.data.EventsRepository
import io2.hobbymatch.events.domain.Event
import io2.hobbymatch.events.domain.Location
import io2.hobbymatch.hobby.data.HobbyRepository
import io2.hobbymatch.hobby.domain.Hobby
import io2.hobbymatch.user.data.UserRepository
import io2.hobbymatch.user.domain.UserInEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEventState(
    val title: String = "",
    val titleError: String? = null,
    
    val description: String = "",
    
    val startTime: String = "",
    val startTimeError: String? = null,
    
    val endTime: String = "",
    val endTimeError: String? = null,
    
    val latitude: String = "",
    val longitude: String = "",
    val locationError: String? = null,
    
    val price: String = "0.0",
    val priceError: String? = null,
    
    val minUsers: Int = 2,
    val maxUsers: Int = 10,
    val usersError: String? = null,
    
    val availableHobbies: List<Hobby> = emptyList(),
    val selectedHobbies: List<Hobby> = emptyList(),
    val hobbiesError: String? = null,
    val isHobbySelectionOpen: Boolean = false,
    
    val isLoading: Boolean = false,
    val isLoadingHobbies: Boolean = false,
    val error: String? = null,
    val eventCreated: Boolean = false
)

sealed class AddEventEvent {
    data class TitleChanged(val title: String) : AddEventEvent()
    data class DescriptionChanged(val description: String) : AddEventEvent()
    data class StartTimeChanged(val startTime: String) : AddEventEvent()
    data class EndTimeChanged(val endTime: String) : AddEventEvent()
    data class LatitudeChanged(val latitude: String) : AddEventEvent()
    data class LongitudeChanged(val longitude: String) : AddEventEvent()
    data class PriceChanged(val price: String) : AddEventEvent()
    data class MinUsersChanged(val minUsers: Int) : AddEventEvent()
    data class MaxUsersChanged(val maxUsers: Int) : AddEventEvent()
    data class ToggleHobbySelection(val hobby: Hobby) : AddEventEvent()
    data object ToggleHobbySelectionDialog : AddEventEvent()
    data object LoadHobbies : AddEventEvent()
    data object Submit : AddEventEvent()
    data object ClearError : AddEventEvent()
}

class AddEventViewModel(
    private val eventsRepository: EventsRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val hobbyRepository: HobbyRepository
) : ScreenModel {
    
    private val _state = MutableStateFlow(AddEventState())
    val state: StateFlow<AddEventState> = _state.asStateFlow()
    
    init {
        loadHobbies()
    }
    
    fun onEvent(event: AddEventEvent) {
        when (event) {
            is AddEventEvent.TitleChanged -> {
                _state.update { it.copy(
                    title = event.title,
                    titleError = if (event.title.isBlank()) "Tytuł jest wymagany" else null
                ) }
            }
            is AddEventEvent.DescriptionChanged -> {
                _state.update { it.copy(description = event.description) }
            }
            is AddEventEvent.StartTimeChanged -> {
                _state.update { it.copy(
                    startTime = event.startTime,
                    startTimeError = validateDateTime(event.startTime)
                ) }
            }
            is AddEventEvent.EndTimeChanged -> {
                _state.update { it.copy(
                    endTime = event.endTime,
                    endTimeError = validateDateTime(event.endTime)
                ) }
            }
            is AddEventEvent.LatitudeChanged -> {
                _state.update { it.copy(
                    latitude = event.latitude,
                    locationError = validateLocation(event.latitude, state.value.longitude)
                ) }
            }
            is AddEventEvent.LongitudeChanged -> {
                _state.update { it.copy(
                    longitude = event.longitude,
                    locationError = validateLocation(state.value.latitude, event.longitude)
                ) }
            }
            is AddEventEvent.PriceChanged -> {
                _state.update { it.copy(
                    price = event.price,
                    priceError = validatePrice(event.price)
                ) }
            }
            is AddEventEvent.MinUsersChanged -> {
                _state.update { it.copy(
                    minUsers = event.minUsers,
                    usersError = if (event.minUsers > state.value.maxUsers) 
                                    "Minimalna liczba uczestników nie może być większa niż maksymalna"
                                 else null
                ) }
            }
            is AddEventEvent.MaxUsersChanged -> {
                _state.update { it.copy(
                    maxUsers = event.maxUsers,
                    usersError = if (state.value.minUsers > event.maxUsers) 
                                    "Minimalna liczba uczestników nie może być większa niż maksymalna"
                                 else null
                ) }
            }
            is AddEventEvent.ToggleHobbySelection -> {
                val currentSelected = state.value.selectedHobbies.toMutableList()
                if (currentSelected.contains(event.hobby)) {
                    currentSelected.remove(event.hobby)
                } else {
                    currentSelected.add(event.hobby)
                }
                _state.update { it.copy(
                    selectedHobbies = currentSelected,
                    hobbiesError = if (currentSelected.isEmpty()) "Wybierz przynajmniej jedno hobby" else null
                ) }
            }
            is AddEventEvent.ToggleHobbySelectionDialog -> {
                _state.update { it.copy(isHobbySelectionOpen = !it.isHobbySelectionOpen) }
            }
            is AddEventEvent.LoadHobbies -> {
                loadHobbies()
            }
            is AddEventEvent.Submit -> {
                submitEvent()
            }
            is AddEventEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }
    
    private fun loadHobbies() {
        screenModelScope.launch {
            _state.update { it.copy(isLoadingHobbies = true) }
            try {
                val hobbies = hobbyRepository.getHobbies()
                _state.update { it.copy(
                    availableHobbies = hobbies,
                    isLoadingHobbies = false
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Nie udało się pobrać listy hobby: ${e.message}",
                    isLoadingHobbies = false
                ) }
            }
        }
    }
    
    private fun submitEvent() {
        val currentState = state.value
        
        // Walidacja wszystkich pól
        val titleError = if (currentState.title.isBlank()) "Tytuł jest wymagany" else null
        val startTimeError = validateDateTime(currentState.startTime)
        val endTimeError = validateDateTime(currentState.endTime)
        val locationError = validateLocation(currentState.latitude, currentState.longitude)
        val priceError = validatePrice(currentState.price)
        val hobbiesError = if (currentState.selectedHobbies.isEmpty()) "Wybierz przynajmniej jedno hobby" else null
        val usersError = if (currentState.minUsers > currentState.maxUsers) 
                            "Minimalna liczba uczestników nie może być większa niż maksymalna"
                         else null
        
        // Aktualizacja stanu z błędami
        _state.update { it.copy(
            titleError = titleError,
            startTimeError = startTimeError,
            endTimeError = endTimeError,
            locationError = locationError,
            priceError = priceError,
            hobbiesError = hobbiesError,
            usersError = usersError
        ) }
        
        // Sprawdzenie czy są jakiekolwiek błędy
        if (titleError != null || startTimeError != null || endTimeError != null || 
            locationError != null || priceError != null || hobbiesError != null || usersError != null) {
            return
        }
        
        // Pobieranie danych użytkownika
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val currentUser = userRepository.loadAuthenticatedUser() ?: throw Exception("Użytkownik nie jest zalogowany")
                
                val latitude = currentState.latitude.toDoubleOrNull() ?: 0.0
                val longitude = currentState.longitude.toDoubleOrNull() ?: 0.0
                val price = currentState.price.toDoubleOrNull() ?: 0.0
                
                // Utworzenie obiektu Event
                val newEvent = Event(
                    id = 0, // ID zostanie nadane przez serwer
                    organizer = UserInEvent(
                        id = currentUser.id.toLong(),
                        name = currentUser.name
                    ),
                    participants = emptyList(), // Początkowo nie ma uczestników
                    title = currentState.title,
                    description = currentState.description.takeIf { it.isNotBlank() },
                    location = Location(
                        latitude = latitude,
                        longitude = longitude
                    ),
                    startTime = currentState.startTime,
                    endTime = currentState.endTime,
                    price = price,
                    hobbies = currentState.selectedHobbies,
                    minUsers = currentState.minUsers,
                    maxUsers = currentState.maxUsers
                )
                
                // Wywołanie repozytorium do utworzenia eventu
                val createdEvent = eventsRepository.createEvent(newEvent)
                
                // Aktualizacja stanu
                _state.update { it.copy(
                    isLoading = false,
                    eventCreated = true
                ) }
                
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Wystąpił błąd podczas tworzenia wydarzenia"
                ) }
            }
        }
    }
    
    private fun validateDateTime(dateTime: String): String? {
        if (dateTime.isBlank()) {
            return "Data i czas są wymagane"
        }
        
        // Prosty regex sprawdzający format YYYY-MM-DDTHH:MM:SS
        val dateTimeRegex = """\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}""".toRegex()
        if (!dateTimeRegex.matches(dateTime)) {
            return "Niepoprawny format. Użyj: YYYY-MM-DDTHH:MM:SS"
        }
        
        return null
    }
    
    private fun validateLocation(latitude: String, longitude: String): String? {
        if (latitude.isBlank() || longitude.isBlank()) {
            return "Lokalizacja jest wymagana"
        }
        
        val lat = latitude.toDoubleOrNull()
        val lon = longitude.toDoubleOrNull()
        
        if (lat == null || lon == null) {
            return "Wprowadź poprawne wartości liczbowe"
        }
        
        if (lat < -90 || lat > 90) {
            return "Szerokość geograficzna musi być między -90 a 90"
        }
        
        if (lon < -180 || lon > 180) {
            return "Długość geograficzna musi być między -180 a 180"
        }
        
        return null
    }
    
    private fun validatePrice(price: String): String? {
        if (price.isBlank()) {
            return "Cena jest wymagana"
        }
        
        val priceValue = price.toDoubleOrNull()
        if (priceValue == null) {
            return "Wprowadź poprawną wartość liczbową"
        }
        
        if (priceValue < 0) {
            return "Cena nie może być ujemna"
        }
        
        return null
    }
}
