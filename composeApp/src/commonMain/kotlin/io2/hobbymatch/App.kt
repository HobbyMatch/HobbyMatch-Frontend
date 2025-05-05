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
import io2.hobbymatch.auth.data.local.realm.AuthMongoDB
import io2.hobbymatch.auth.data.remote.AuthApiService
import io2.hobbymatch.auth.data.remote.MockAuthApiService
import io2.hobbymatch.auth.presentation.AuthScreen
import io2.hobbymatch.auth.presentation.AuthViewModel
import io2.hobbymatch.business.data.BusinessClientRepository
import io2.hobbymatch.business.data.remote.BusinessClientApiService
import io2.hobbymatch.business.data.remote.MockBusinessClientApiService
import io2.hobbymatch.business.presentation.BusinessClientViewModel
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
        Navigator(AuthScreen()) {
            SlideTransition(it)
        }
    }
}

val appModule = module {
    // Provide Realm Database instances as singletons
    single { UserMongoDB() } // For user-related local data
    single { AuthMongoDB() } // For authentication-related local data

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
    single<BusinessClientApiService> { MockBusinessClientApiService() }

    // Provide UserRepository (used by UserViewModel)
    single { UserRepository(get<UserApiService>(), get<UserMongoDB>()) }

    single{ BusinessClientRepository(get<BusinessClientApiService>()) }

    // Provide Auth-related dependencies
    single<AuthApiService> { MockAuthApiService() }
    single { AuthRepository(get<AuthApiService>(), get<AuthMongoDB>()) }

    // Register ViewModels
    factory { UserViewModel(get<UserRepository>(), get<AuthRepository>()) } // ViewModel for the user screen
    factory { AuthViewModel(get<AuthRepository>()) } // ViewModel for login
    factory { ActivityViewModel(get<AuthMongoDB>()) } // ViewModel for activity
    factory { BusinessClientViewModel(get<BusinessClientRepository>(), get<AuthRepository>()) } // ViewModel for business client}
}

fun initializeKoin() {
    startKoin {
        printLogger(level = Level.DEBUG)
        modules(appModule)
    }
}

