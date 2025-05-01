package io2.hobbymatch

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io2.hobbymatch.activity.presentation.ActivityViewModel
import io2.hobbymatch.auth.data.AuthRepository
import io2.hobbymatch.auth.data.local.realm.LoginMongoDB
import io2.hobbymatch.auth.data.remote.AuthApiService
import io2.hobbymatch.auth.data.remote.MockAuthApiService
import io2.hobbymatch.auth.presentation.LoginScreen
import io2.hobbymatch.auth.presentation.LoginViewModel
import io2.hobbymatch.network.ApiConfig
import io2.hobbymatch.ui.theme.darkScheme
import io2.hobbymatch.ui.theme.lightScheme
import io2.hobbymatch.user.data.local.realm.UserMongoDB
// import io2.hobbymatch.user.presentation.UserViewModel
import kotlinx.serialization.json.Json
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

val appModule = module {
    // Register Realm DB instances as singletons
    single { UserMongoDB() }
    single { LoginMongoDB() }

    // Provide ApiConfig for endpoint management
    single {
        ApiConfig.DEVELOPMENT
    }

    // Provide HttpClient for networking
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 1000L
            }
        }
    }

    // Provide AuthApiService implementation
    single<AuthApiService> { MockAuthApiService() }

    // Provide AuthRepository with its required dependencies
    single { AuthRepository(get(), get<LoginMongoDB>()) }

    // Provide ViewModels
    // factory { UserViewModel(get<UserMongoDB>(), get<LoginMongoDB>()) }
    factory { LoginViewModel(get<AuthRepository>()) }
    factory { ActivityViewModel(get<LoginMongoDB>()) }
}


fun initializeKoin() {
    startKoin {
        modules(appModule)
    }
}