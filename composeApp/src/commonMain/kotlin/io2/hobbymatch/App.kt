package io2.hobbymatch

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io2.hobbymatch.login.data.local.realm.LoginMongoDB
import io2.hobbymatch.login.data.remote.LoginApiService
import io2.hobbymatch.login.data.remote.LoginApiServiceImpl
import io2.hobbymatch.login.domain.repository.LoginRepository
import io2.hobbymatch.login.domain.repository.LoginRepositoryImpl
import io2.hobbymatch.login.presentation.LoginScreen
import io2.hobbymatch.login.presentation.LoginViewModel
import io2.hobbymatch.network.networkModule
import io2.hobbymatch.ui.theme.darkScheme
import io2.hobbymatch.ui.theme.lightScheme
import io2.hobbymatch.user.data.local.realm.UserMongoDB
import io2.hobbymatch.user.data.remote.UserApiService
import io2.hobbymatch.user.data.remote.UserApiServiceImpl
import io2.hobbymatch.user.domain.repository.UserRepository
import io2.hobbymatch.user.domain.repository.UserRepositoryImpl
import io2.hobbymatch.user.presentation.UserViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("ktlint:standard:function-naming")
@Composable
@Preview
fun App() {
    initializeKoin()

    // Set up the theme based on the system settings
    val colors by mutableStateOf(
        if (isSystemInDarkTheme()) darkScheme else lightScheme,
    )

    MaterialTheme(colorScheme = colors) {
        Navigator(LoginScreen()) {
            SlideTransition(it)
        }
    }
}

val mongoModule =
    module {
        // Register BOTH MongoDB instances as singletons
        single { UserMongoDB() }
        single { LoginMongoDB() }

        // Inject the correct MongoDB instance into each ViewModel
        factory { UserViewModel(get<UserMongoDB>(), get<LoginMongoDB>()) }
        factory { LoginViewModel(get<LoginMongoDB>()) }
    }

// Połącz wszystkie moduły
fun initializeKoin() {
    startKoin {
        modules(
            networkModule, // Dodaj moduł sieciowy
            appModule, // Przemianuj stary moduł lub dodaj nowy
        )
    }
}

// Stary 'mongoModule' można przemianować lub połączyć
val appModule =
    module {
        // --- Baza Danych ---
        single { UserMongoDB() }
        single { LoginMongoDB() }

        // --- Sieć (ApiService) ---
        single<LoginApiService> { LoginApiServiceImpl(get()) } // Wstrzyknij HttpClient
        single<UserApiService> { UserApiServiceImpl(get()) } // Wstrzyknij HttpClient

        // --- Repozytoria ---
        single<LoginRepository> { LoginRepositoryImpl(get(), get()) } // Wstrzyknij ApiService i MongoDB
        single<UserRepository> { UserRepositoryImpl(get(), get()) } // Wstrzyknij ApiService i MongoDB

        // --- ViewModels ---
//    // Teraz wstrzykuj Repozytoria zamiast MongoDB
//    factory { UserViewModel(get<UserRepository>()) }     // Wstrzyknij UserRepository
//    factory { LoginViewModel(get<LoginRepository>()) }  // Wstrzyknij LoginRepository

        factory { UserViewModel(get<UserMongoDB>(), get<LoginMongoDB>()) } // Wstrzyknij UserRepository
        factory { LoginViewModel(get<LoginMongoDB>()) }
    }
