package io2.hobbymatch

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io2.hobbymatch.activity.presentation.ActivityViewModel
import io2.hobbymatch.login.data.local.realm.LoginMongoDB
import io2.hobbymatch.login.presentation.LoginScreen
import io2.hobbymatch.login.presentation.LoginViewModel
import io2.hobbymatch.ui.theme.darkScheme
import io2.hobbymatch.ui.theme.lightScheme
import io2.hobbymatch.user.data.local.realm.UserMongoDB
import io2.hobbymatch.user.presentation.UserViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Composable
@Preview
fun App() {
    initializeKoin()

    // Set up the theme based on the system settings
    val colors by mutableStateOf(
        if(isSystemInDarkTheme()) darkScheme else lightScheme
    )

    MaterialTheme(colorScheme = colors) {
        Navigator(LoginScreen()) {
            SlideTransition(it)
        }
    }
}

val mongoModule = module {
    // Register BOTH MongoDB instances as singletons
    single { UserMongoDB() }
    single { LoginMongoDB() }

    // Inject the correct MongoDB instance into each ViewModel
    factory { UserViewModel(get<UserMongoDB>(), get<LoginMongoDB>()) }
    factory { LoginViewModel(get<LoginMongoDB>()) }
    factory { ActivityViewModel(get<LoginMongoDB>()) }
}


fun initializeKoin() {
    startKoin {
        modules(mongoModule)
    }
}