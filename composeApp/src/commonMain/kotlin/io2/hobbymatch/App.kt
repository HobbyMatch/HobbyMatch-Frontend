package io2.hobbymatch

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io2.hobbymatch.ui.theme.darkScheme
import io2.hobbymatch.ui.theme.lightScheme
import io2.hobbymatch.user.presentation.UserScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // Set up the theme based on the system settings
    val colors by mutableStateOf(
        if(isSystemInDarkTheme()) darkScheme else lightScheme
    )

    MaterialTheme(colors) {
        Navigator(UserScreen()) {
            SlideTransition(it)
        }
    }
}