package io2.hobbymatch.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io2.hobbymatch.activity.presentation.ActivityViewModel
import io2.hobbymatch.auth.data.AuthRepository
import io2.hobbymatch.auth.data.local.room.AuthDatabase
import io2.hobbymatch.auth.data.local.room.RoomAuthDataSource
import io2.hobbymatch.auth.data.local.room.getAuthRoomDatabase
import io2.hobbymatch.auth.data.remote.AuthApiService
import io2.hobbymatch.auth.data.remote.MockAuthApiService
import io2.hobbymatch.auth.presentation.AuthViewModel
import io2.hobbymatch.business.data.BusinessClientRepository
import io2.hobbymatch.business.data.remote.BusinessClientApiService
import io2.hobbymatch.business.data.remote.MockBusinessClientApiService
import io2.hobbymatch.business.presentation.BusinessClientViewModel
import io2.hobbymatch.network.ApiConfig
import io2.hobbymatch.user.data.UserRepository
import io2.hobbymatch.user.data.local.room.UserDatabase
import io2.hobbymatch.user.data.local.room.UserRoomDataSource
import io2.hobbymatch.user.data.local.room.getUserRoomDatabase
import io2.hobbymatch.user.data.remote.MockUserApiService
import io2.hobbymatch.user.data.remote.UserApiService
import io2.hobbymatch.user.presentation.UserViewModel
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect val targetDatabaseModule : Module

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        printLogger(level = Level.DEBUG)
        config?.invoke(this)
        modules(targetDatabaseModule, appModule)
    }
}

val appModule = module {
    single { getAuthRoomDatabase(get(named("AuthBuilder"))) }
    single { getUserRoomDatabase(get(named("UserBuilder"))) }

    // Provide Room Database instances as singletons
    single { RoomAuthDataSource(get<AuthDatabase>().authDao()) }
    single { UserRoomDataSource(get<UserDatabase>()) }

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
    single { UserRepository(get<UserApiService>()/*, get<UserMongoDB>()*/) }

    single { BusinessClientRepository(get<BusinessClientApiService>()) }

    // Provide Auth-related dependencies
    single<AuthApiService> { MockAuthApiService() }
    single { AuthRepository(get<AuthApiService>(), get<RoomAuthDataSource>()) }

    // Register ViewModels
    factory {
        UserViewModel(
            get<UserRepository>(),
            get<AuthRepository>()
        )
    } // ViewModel for the user screen
    factory { AuthViewModel(get<AuthRepository>()) } // ViewModel for login
    factory { ActivityViewModel(/*get<AuthMongoDB>()*/) } // ViewModel for activity
    single {
        BusinessClientViewModel(
            get<BusinessClientRepository>(),
            get<AuthRepository>()
        )
    } // ViewModel for business client}
}