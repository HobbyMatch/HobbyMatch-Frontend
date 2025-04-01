package io2.hobbymatch.user.presentation

// Import ScreenModel and screenModelScope
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io2.hobbymatch.user.data.local.realm.MongoDB
import io2.hobbymatch.user.data.local.realm.RealmDatabase
import io2.hobbymatch.user.data.local.realm.UserProfileRealm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch // Import launch

// State and Event classes remain the same...
data class UserScreenState(
    // Personal information
    val email: String = "",
    val username: String = "",
    val name: String = "",
    val surname: String = "",
    val hobbies: List<String> = emptyList(),
    val birthday: String = "",
    val gender: String = "",
    val bio: String = "",
    // Add other fields as needed, mainly functional
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false // Assuming this would be set based on auth state
)

sealed class UserUiEvent {
    data class EnterUsername(val username: String) : UserUiEvent()
    data class EnterEmail(val email: String) : UserUiEvent()
    data class EnterName(val name: String) : UserUiEvent()
    data class EnterSurname(val surname: String) : UserUiEvent()
    data class AddHobby(val hobby: String) : UserUiEvent()
    data class RemoveHobby(val hobby: String) : UserUiEvent()
    data class EnterBirthday(val birthday: String) : UserUiEvent()
    data class EnterGender(val gender: String) : UserUiEvent()
    data class EnterBio(val bio: String) : UserUiEvent()
    data object Save : UserUiEvent()
    // Maybe add Load event if data needs fetching initially
    // data object Load : UserUiEvent()
}


// Implement ScreenModel instead of ViewModel
class UserViewModel(private val mongoDB: MongoDB? = null) : ScreenModel {
    // Get the Realm instance from the singleton
    private val realm: Realm = RealmDatabase.instance // <-- Access Realm here

    private var _state = MutableStateFlow(UserScreenState())
    val state: StateFlow<UserScreenState> = _state.stateIn(
        // Use screenModelScope
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = UserScreenState()
    )

    // --- init block and onEvent function remain the same ---
    init {
        loadUserData() // Load data when ViewModel is created
    }


    fun onEvent(event: UserUiEvent) {
        // Use screenModelScope for coroutines if needed inside event handlers
        when (event) {
            is UserUiEvent.EnterUsername -> _state.value = _state.value.copy(username = event.username)
            is UserUiEvent.EnterEmail -> _state.value = _state.value.copy(email = event.email)
            is UserUiEvent.EnterName -> _state.value = _state.value.copy(name = event.name)
            is UserUiEvent.EnterSurname -> _state.value = _state.value.copy(surname = event.surname)
            is UserUiEvent.EnterBirthday -> _state.value = _state.value.copy(birthday = event.birthday)
            is UserUiEvent.EnterGender -> _state.value = _state.value.copy(gender = event.gender)
            is UserUiEvent.EnterBio -> _state.value = _state.value.copy(bio = event.bio)

            is UserUiEvent.AddHobby -> {
                if (event.hobby.isNotBlank() && event.hobby !in _state.value.hobbies) {
                    _state.value = _state.value.copy(hobbies = _state.value.hobbies + event.hobby.trim())
                }
            }
            is UserUiEvent.RemoveHobby -> {
                _state.value = _state.value.copy(hobbies = _state.value.hobbies - event.hobby)
            }
            UserUiEvent.Save -> saveUserData()
            // Handle Load event if added
            // UserUiEvent.Load -> loadUserData()
        }
    }

    // --- saveUserData and loadUserData now use the 'realm' property defined above ---
    private fun saveUserData() {
        val currentUserState = state.value

        screenModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true, isError = false, errorMessage = null)
            try {
                // Use the 'realm' property from the class
                realm.write {
                    val existingProfile: UserProfileRealm? =
                        this.query<UserProfileRealm>("id == $0", "SINGLE_USER_PROFILE").first().find()

                    if (existingProfile != null) {
                        // --- Update Existing Profile ---
                        existingProfile.email = currentUserState.email
                        // --- Start Added Code ---
                        existingProfile.username = currentUserState.username
                        existingProfile.name = currentUserState.name
                        existingProfile.surname = currentUserState.surname
                        existingProfile.birthday = currentUserState.birthday
                        existingProfile.gender = currentUserState.gender
                        existingProfile.bio = currentUserState.bio
                        // --- End Added Code ---
                        // Update hobbies list: Clear existing and add current ones
                        existingProfile.hobbies.clear()
                        existingProfile.hobbies.addAll(currentUserState.hobbies)
                    } else {
                        // --- Create New Profile ---
                        this.copyToRealm(UserProfileRealm().apply {
                            // id is set by default ("SINGLE_USER_PROFILE")
                            // --- Start Added Code ---
                            email = currentUserState.email
                            username = currentUserState.username
                            name = currentUserState.name
                            surname = currentUserState.surname
                            birthday = currentUserState.birthday
                            gender = currentUserState.gender
                            bio = currentUserState.bio
                            // --- End Added Code ---
                            // Add hobbies
                            hobbies.addAll(currentUserState.hobbies)
                        })
                    }
                }
                launch(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Failed to save profile: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    private fun loadUserData() {
        screenModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true, isError = false, errorMessage = null)
            try {
                // Use the 'realm' property from the class
                val profile = realm.query<UserProfileRealm>("id == $0", "SINGLE_USER_PROFILE").first().find()

                launch(Dispatchers.Main) {
                    if (profile != null) {
                        // --- Map loaded Realm data to UI State ---
                        _state.value = UserScreenState(
                            // --- Start Added Code ---
                            email = profile.email,
                            username = profile.username,
                            name = profile.name,
                            surname = profile.surname,
                            birthday = profile.birthday,
                            gender = profile.gender,
                            bio = profile.bio,
                            // --- End Added Code ---
                            hobbies = profile.hobbies.toList(), // Convert RealmList to List for state
                            isLoading = false, // Data loaded successfully
                            isError = false,
                            errorMessage = null
                            // isLoggedIn = ... // Set based on actual auth status if available
                        )
                    } else {
                        // No profile found, reset to default state but stop loading
                        _state.value = UserScreenState(isLoading = false) // Keep defaults, just stop loading
                        // Or: _state.value = _state.value.copy(isLoading = false) // Keep potentially entered data
                    }
                }
            } catch(e: Exception) {
                launch(Dispatchers.Main) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Failed to load profile: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }
}