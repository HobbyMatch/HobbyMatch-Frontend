package io2.hobbymatch.user.presentation
//
//// Import ScreenModel and screenModelScope
//import cafe.adriel.voyager.core.model.ScreenModel
//import cafe.adriel.voyager.core.model.screenModelScope
//import io2.hobbymatch.auth.data.local.realm.LoginMongoDB
//import io2.hobbymatch.user.data.local.realm.UserMongoDB
//import io2.hobbymatch.user.data.local.realm.UserProfileRealm
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.launchIn
//import kotlinx.coroutines.flow.onEach
//import kotlinx.coroutines.flow.onStart
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch // Import launch
//
//// State and Event classes remain the same...
//data class UserScreenState(
//    // Personal information
//    val email: String = "",
//    val username: String = "",
//    val name: String = "",
//    val surname: String = "",
//    val hobbies: List<String> = emptyList(),
//    val birthday: String = "",
//    val bio: String = "",
//    // Add other fields as needed, mainly functional
//    val isLoading: Boolean = false,
//    val isError: Boolean = false,
//    val errorMessage: String? = null,
//    val isLoggedIn: Boolean = false // Assuming this would be set based on auth state
//)
//
//sealed class UserUiEvent {
//    data class EnterUsername(val username: String) : UserUiEvent()
//    data class EnterEmail(val email: String) : UserUiEvent()
//    data class EnterName(val name: String) : UserUiEvent()
//    data class EnterSurname(val surname: String) : UserUiEvent()
//    data class AddHobby(val hobby: String) : UserUiEvent()
//    data class RemoveHobby(val hobby: String) : UserUiEvent()
//    data class EnterBirthday(val birthday: String) : UserUiEvent()
//    data class EnterBio(val bio: String) : UserUiEvent()
//    data object ResetToken : UserUiEvent()
//    data object Save : UserUiEvent()
//    // Maybe add Load event if data needs fetching initially
//    // data object Load : UserUiEvent()
//}
//
//class UserViewModel(
//    private val userMongoDB: UserMongoDB,
//    private val loginMongoDB: LoginMongoDB
//) : ScreenModel {
//
//    // MutableStateFlow for internal state management
//    private val _state = MutableStateFlow(UserScreenState())
//    // Expose StateFlow publicly - no need for stateIn if collecting a flow below
//    val state: StateFlow<UserScreenState> = _state.asStateFlow()
//
//    init {
//        // Observe user profile changes reactively
//        observeUserProfile()
//    }
//
//    // Observe changes from the database using Flow
//    private fun observeUserProfile() {
//        // Assuming mongoDB is non-null due to injection
//        userMongoDB.getUserProfileFlow()
//            .onStart {
//                // Can confirm loading state, though initial state covers it
//                _state.update { it.copy(isLoading = true, isError = false, errorMessage = null) }
//            }
//            .onEach { profile ->
//                // Map the Realm object (or null) to the UI State
//                if (profile != null) {
//                    _state.value = UserScreenState(
//                        email = profile.email,
//                        username = profile.username,
//                        name = profile.name,
//                        surname = profile.surname,
//                        birthday = profile.birthday,
//                        bio = profile.bio,
//                        hobbies = profile.hobbies.toList(), // Convert RealmList to List
//                        isLoading = false, // Data loaded/updated
//                        isError = false,
//                        errorMessage = null
//                    )
//                } else {
//                    // No profile found, reset to default non-loading state
//                    // Keep potentially entered data if needed, or reset fully:
//                    _state.value = UserScreenState(isLoading = false, isError = false) // Example: Full reset
//                    // Or only update loading/error state:
//                    // _state.update { it.copy(isLoading = false, isError = false, errorMessage = null) }
//                }
//            }
//            .catch { e ->
//                // Handle errors during Flow collection
//                _state.update { it.copy(
//                    isLoading = false,
//                    isError = true,
//                    errorMessage = "Failed to load profile: ${e.message ?: "Unknown error"}"
//                )}
//            }
//            // Collect the flow within the screenModelScope
//            .launchIn(screenModelScope)
//    }
//
//    fun onEvent(event: UserUiEvent) {
//        // Use .update for atomic state updates
//        when (event) {
//            is UserUiEvent.EnterUsername -> _state.update { it.copy(username = event.username) }
//            is UserUiEvent.EnterEmail -> _state.update { it.copy(email = event.email) }
//            is UserUiEvent.EnterName -> _state.update { it.copy(name = event.name) }
//            is UserUiEvent.EnterSurname -> _state.update { it.copy(surname = event.surname) }
//            is UserUiEvent.EnterBirthday -> _state.update { it.copy(birthday = event.birthday) }
//            is UserUiEvent.EnterBio -> _state.update { it.copy(bio = event.bio) }
//
//            is UserUiEvent.AddHobby -> {
//                if (event.hobby.isNotBlank() && event.hobby !in _state.value.hobbies) {
//                    _state.update { it.copy(hobbies = it.hobbies + event.hobby.trim()) }
//                }
//            }
//            is UserUiEvent.RemoveHobby -> {
//                _state.update { it.copy(hobbies = it.hobbies - event.hobby) }
//            }
//            UserUiEvent.Save -> saveUserData() // Call the refactored save function
//            UserUiEvent.ResetToken -> resetLoginToken() // Handle the reset event
//        }
//    }
//
//    // Refactored saveUserData using injected mongoDB
//    private fun saveUserData() {
//        val currentUserState = state.value // Get current UI state
//
//        // Create a UserProfileRealm object from the current UI state
//        // The ID ("SINGLE_USER_PROFILE") will be handled by mongoDB.saveUserProfile
//        val profileToSave = UserProfileRealm().apply {
//            email = currentUserState.email
//            username = currentUserState.username
//            name = currentUserState.name
//            surname = currentUserState.surname
//            birthday = currentUserState.birthday
//            bio = currentUserState.bio
//            hobbies.addAll(currentUserState.hobbies)
//        }
//
//        screenModelScope.launch { // Launch in default scope, IO is handled by MongoDB class
//            // Set loading state before saving
//            _state.update { it.copy(isLoading = true, isError = false, errorMessage = null) }
//            try {
//                // Call the save function in MongoDB class (assuming non-null)
//                userMongoDB.saveUserProfile(profileToSave)
//
//                // On successful save, the Flow observed in observeUserProfile
//                // should automatically emit the new state, updating the UI
//                // and setting isLoading = false.
//                // We might only need to manually turn off loading if there's an error.
//                // _state.update { it.copy(isLoading = false) } // Usually not needed here if Flow works
//
//            } catch (e: Exception) {
//                // Update state on error
//                _state.update { it.copy(
//                    isLoading = false,
//                    isError = true,
//                    errorMessage = "Failed to save profile: ${e.message ?: "Unknown error"}"
//                )}
//            }
//        }
//    }
//
//    // Reset Login Token using the injected LoginMongoDB
//    private fun resetLoginToken() {
//        screenModelScope.launch {
//            _state.update { it.copy(isLoading = true, errorMessage = null) } // Indicate working
//            try {
//                loginMongoDB.resetLoginToken() // Call the reset method
//                _state.update { it.copy(isLoading = false) }
//                // NOTE: After token reset, the UI should ideally navigate away.
//                // This ViewModel should not handle navigation directly.
//                // The UI layer (e.g., App observing auth state) should react.
//                // You might want to clear the user profile fields here too:
//                // _state.value = UserScreenState(isLoading = false) // Reset state
//
//            } catch (e: Exception) {
//                _state.update { it.copy(
//                    isLoading = false,
//                    isError = true,
//                    errorMessage = "Logout Failed: ${e.message ?: "Unknown error"}"
//                )}
//            }
//        }
//    }
//
//    // Clean up Realm connection when ViewModel is cleared
//    fun onCleared() {
//        // Assuming mongoDB instance is non-null
//        userMongoDB.close()
//    }
//}