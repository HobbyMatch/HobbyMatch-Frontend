package io2.hobbymatch.login.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io2.hobbymatch.login.data.local.realm.LoginMongoDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class LoginScreenState(
    val email: String = "",
    val token: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

sealed class LoginUiEvent {
    data object ClickLogin : LoginUiEvent()
}

class LoginViewModel(private val loginMongoDB: LoginMongoDB) : ScreenModel {

    private var _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = LoginScreenState()
    )

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            LoginUiEvent.ClickLogin -> { /* TODO - networking will be here */ }
        }
    }


    /* ======================== WRITE CODE BELOW ======================== */

}