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
import io2.hobbymatch.user.data.UserRepository
import io2.hobbymatch.user.data.local.realm.UserMongoDB
import io2.hobbymatch.user.data.remote.MockUserApiService
import io2.hobbymatch.user.data.remote.UserApiService
import io2.hobbymatch.user.presentation.UserViewModel
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

@OptIn(KoinInternalApi::class)
@Composable
@Preview
fun App() {
    initializeKoin()
    println(appModule.includedModules)

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
    // Provide Realm Database instances as singletons
    single { UserMongoDB() } // For user-related local data
    single { LoginMongoDB() } // For authentication-related local data

    // Provide API configuration
    single { ApiConfig.DEVELOPMENT }

    // Provide HttpClient for network operations
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000L
            }
        }
    }

    // Bind User API Service (use MockUserApiService for now)
    single<UserApiService> { MockUserApiService() }

    // Provide UserRepository (used by UserViewModel)
    single { UserRepository(get<UserApiService>(), get<UserMongoDB>()) }

    // Provide Auth-related dependencies
    single<AuthApiService> { MockAuthApiService() }
    single { AuthRepository(get<AuthApiService>(), get<LoginMongoDB>()) }

    // Register ViewModels
    factory { UserViewModel(get<UserRepository>(), get<AuthRepository>()) } // ViewModel for the user screen
    factory { LoginViewModel(get<AuthRepository>()) } // ViewModel for login
    factory { ActivityViewModel(get<LoginMongoDB>()) } // ViewModel for activity
}

fun initializeKoin() {
    startKoin {
        printLogger(level = Level.DEBUG)
        modules(appModule)
    }
}

