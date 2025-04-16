package io2.hobbymatch.di

import io2.hobbymatch.login.data.local.TokenLocalDataSource
import io2.hobbymatch.login.data.local.realm.RealmTokenLocalDataSource
import io2.hobbymatch.login.data.remote.AuthRemoteDataSource
import io2.hobbymatch.login.data.remote.ktor.KtorAuthRemoteDataSource
import io2.hobbymatch.login.data.repository.AuthRepository
import io2.hobbymatch.login.data.repository.AuthRepositoryImpl
import io2.hobbymatch.login.data.repository.TokenRepository
import io2.hobbymatch.login.data.repository.TokenRepositoryImpl
import io2.hobbymatch.login.domain.usecase.LoadAppTokenUseCase
import io2.hobbymatch.login.domain.usecase.LogoutUseCase
import io2.hobbymatch.login.domain.usecase.ObserveAppTokenUseCase
import io2.hobbymatch.login.domain.usecase.ValidateGoogleTokenUseCase
import io2.hobbymatch.login.presentation.LoginViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val loginModule = module {
    println("Initializing loginModule...") // Log pomocniczy
    // DataSources
    factory<TokenLocalDataSource> { RealmTokenLocalDataSource(get()) } // Pobiera LoginMongoDB
    factory<AuthRemoteDataSource> { KtorAuthRemoteDataSource(get(), get()) } // Pobiera HttpClient i baseUrl

    // Repositories
    factory<TokenRepository> { TokenRepositoryImpl(get()) } // Pobiera TokenLocalDataSource
    factory<AuthRepository> { AuthRepositoryImpl(get()) } // Pobiera AuthRemoteDataSource

    // UseCases
    factoryOf(::ValidateGoogleTokenUseCase)
    factoryOf(::LoadAppTokenUseCase)
    factoryOf(::ObserveAppTokenUseCase)
    factoryOf(::LogoutUseCase)

    // ViewModel / ScreenModel
    factory { LoginViewModel(get(), get(), get(), /*get()*/) } // Pobiera UseCase'y
}