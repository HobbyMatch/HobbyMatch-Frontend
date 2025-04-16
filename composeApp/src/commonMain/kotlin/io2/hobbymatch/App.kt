package io2.hobbymatch

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io2.hobbymatch.di.appModule
import io2.hobbymatch.login.presentation.LoginScreen
import io2.hobbymatch.ui.theme.darkScheme
import io2.hobbymatch.ui.theme.lightScheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.startKoin

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

//val mongoModule = module {
//    // Register BOTH MongoDB instances as singletons
//    single { UserMongoDB() }
//    single { LoginMongoDB() }
//
//    // Inject the correct MongoDB instance into each ViewModel
//    factory { UserViewModel(get<UserMongoDB>(), get<LoginMongoDB>()) }
//    factory { LoginViewModel(get<LoginMongoDB>()) }
//    factory { ActivityViewModel(get<LoginMongoDB>()) }
//}


fun initializeKoin() {
    startKoin {
        // Tutaj ładujemy WSZYSTKIE moduły aplikacji
        modules(appModule) // Główny moduł agregujący inne
    }
    println("Koin Initialized!") // Log pomocniczy
}

//package io2.hobbymatch
//
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.* // Importuj wszystko z runtime dla getValue/mutableStateOf
//import cafe.adriel.voyager.navigator.Navigator
//import cafe.adriel.voyager.transitions.SlideTransition
//import io2.hobbymatch.di.appModule // Zaimportuj główny moduł aplikacji (zdefiniowany poniżej)
//import io2.hobbymatch.login.presentation.LoginScreen
//import io2.hobbymatch.ui.theme.darkScheme
//import io2.hobbymatch.ui.theme.lightScheme
//import org.jetbrains.compose.ui.tooling.preview.Preview
//import org.koin.core.context.startKoin
//
//// Zmienna globalna do sprawdzania, czy Koin został już zainicjowany
//private var isKoinInitialized = false
//
//@Composable
//@Preview
//fun App() {
//    // Inicjalizuj Koin tylko raz
//    LaunchedEffect(Unit) {
//        if (!isKoinInitialized) {
//            initializeKoin()
//            isKoinInitialized = true
//        }
//    }
//
//    // Ustawienie motywu
//    val colors by mutableStateOf(
//        if(isSystemInDarkTheme()) darkScheme else lightScheme
//    )
//
//    MaterialTheme(colorScheme = colors) {
//        // Upewnij się, że Koin jest gotowy przed nawigacją (opcjonalne, ale bezpieczniejsze)
//        if (isKoinInitialized) {
//            Navigator(LoginScreen()) { navigator -> // Zmień nazwę zmiennej lambda
//                SlideTransition(navigator) // Użyj poprawnej zmiennej
//            }
//        } else {
//            // Opcjonalnie: Pokaż ekran ładowania, dopóki Koin się nie zainicjalizuje
//            // androidx.compose.material3.CircularProgressIndicator()
//        }
//    }
//}
//
//// Przenieś inicjalizację Koin do osobnej funkcji
//fun initializeKoin() {
//    startKoin {
//        // Tutaj ładujemy WSZYSTKIE moduły aplikacji
//        modules(appModule) // Główny moduł agregujący inne
//    }
//    println("Koin Initialized!") // Log pomocniczy
//}