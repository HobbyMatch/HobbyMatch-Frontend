package io2.hobbymatch.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io2.hobbymatch.auth.data.AuthRepository
import io2.hobbymatch.auth.data.local.room.AuthDatabase
import io2.hobbymatch.auth.data.local.room.AuthRoomDataSource
import io2.hobbymatch.auth.data.local.room.getAuthRoomDatabase
import io2.hobbymatch.auth.data.remote.AuthApiService
import io2.hobbymatch.auth.data.remote.AuthApiServiceImpl
import io2.hobbymatch.auth.presentation.AuthViewModel
import io2.hobbymatch.business.data.BusinessClientRepository
import io2.hobbymatch.business.data.remote.BusinessClientApiService
import io2.hobbymatch.business.data.remote.MockBusinessClientApiService
import io2.hobbymatch.business.presentation.BusinessClientViewModel
import io2.hobbymatch.events.data.EventsRepository
import io2.hobbymatch.events.data.remote.EventsApiService
import io2.hobbymatch.events.data.remote.EventsApiServiceImpl
import io2.hobbymatch.events.presentation.EventsViewModel
import io2.hobbymatch.hobby.data.HobbyRepository
import io2.hobbymatch.hobby.data.remote.HobbyApiService
import io2.hobbymatch.hobby.data.remote.HobbyApiServiceImpl
import io2.hobbymatch.network.ApiConfig
import io2.hobbymatch.user.data.UserRepository
import io2.hobbymatch.user.data.local.room.UserDatabase
import io2.hobbymatch.user.data.local.room.UserRoomDataSource
import io2.hobbymatch.user.data.local.room.getUserRoomDatabase
import io2.hobbymatch.user.data.remote.UserApiService
import io2.hobbymatch.user.data.remote.UserApiServiceImpl
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
        printLogger(level = Level.INFO)
        config?.invoke(this)
        modules(targetDatabaseModule, appModule)
    }
}

val appModule = module {

    /** ========= DATABASES ========= **/

    single { getAuthRoomDatabase(get(named("AuthBuilder"))) }
    single { getUserRoomDatabase(get(named("UserBuilder"))) }

    // Provide Room Database instances as singletons
    single { AuthRoomDataSource(get<AuthDatabase>().authDao()) }
    single { UserRoomDataSource(get<UserDatabase>()) }

    /** ========= API CONFIG ========= **/

    single { ApiConfig.PRODUCTION }

    /** ========= HTTP CLIENT ========= **/

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
            install(Auth) {
                bearer {
                    loadTokens {
                        val authRepo by lazy { get<AuthRepository>() }
                        val accessToken = authRepo.loadAccessToken() ?: ""
                        val refreshToken = authRepo.loadRefreshToken() ?: ""
                        BearerTokens(accessToken = accessToken, refreshToken = refreshToken)
                    }
                    // Linijka,, aby token był wysyłany do wszystkich żądań
                    sendWithoutRequest { true }
                }
            }
        }
    }

    /** ========= API SERVICES ========= **/

    single<UserApiService> { UserApiServiceImpl(
        httpClient = get<HttpClient>(),
        apiConfig = get<ApiConfig>()
    ) }
    single<BusinessClientApiService> { MockBusinessClientApiService() }
    single<HobbyApiService> { HobbyApiServiceImpl(
        httpClient = get<HttpClient>(),
        apiConfig = get<ApiConfig>(),
    ) }
    single<EventsApiService> { EventsApiServiceImpl(
        httpClient = get<HttpClient>(),
        apiConfig = get<ApiConfig>()
    ) }

    single<AuthApiService> { AuthApiServiceImpl(
        httpClient = get<HttpClient>(),
        apiConfig = get<ApiConfig>()
    ) }

    /** ========= REPOSITORIES ========= **/


    single { UserRepository(get<UserApiService>(), get<UserRoomDataSource>()) }

    single { BusinessClientRepository(get<BusinessClientApiService>()) }

    single { HobbyRepository(get<HobbyApiService>()) }

    single { AuthRepository(get<AuthApiService>(), get<AuthRoomDataSource>()) }

    single { EventsRepository(get<EventsApiService>()) }

    /** ========= VIEW MODELS ========= **/

    factory {
        UserViewModel(
            get<UserRepository>(),
            get<AuthRepository>(),
            get<HobbyRepository>()
        )
    }

    factory {
        AuthViewModel(get<AuthRepository>())
    }

    single {
           BusinessClientViewModel(
            get<BusinessClientRepository>(),
            get<AuthRepository>()
        )
    }

    factory {
        EventsViewModel(
            get<UserRepository>(),
            get<HobbyRepository>(),
            get<EventsRepository>(),
            get<AuthRepository>()
        )
    }
}