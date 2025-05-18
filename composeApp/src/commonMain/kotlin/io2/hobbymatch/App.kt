package io2.hobbymatch

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io2.hobbymatch.auth.presentation.AuthScreen
import io2.hobbymatch.di.appModule
import io2.hobbymatch.ui.theme.darkScheme
import io2.hobbymatch.ui.theme.lightScheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.annotation.KoinInternalApi

@OptIn(KoinInternalApi::class)
@Composable
@Preview
fun App() {
    // initializeKoin()
    println(appModule.includedModules)

    // Set up the theme based on the system settings
    val colors by mutableStateOf(
        if(isSystemInDarkTheme()) darkScheme else lightScheme
    )

    MaterialTheme(colorScheme = colors) {
        Navigator(AuthScreen()) {
            SlideTransition(it)
        }
    }
}