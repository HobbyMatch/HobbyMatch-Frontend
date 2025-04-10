package io2.hobbymatch.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

// Podstawowy moduł Koin dostarczający HttpClient
val networkModule =
    module {
        single { provideJson() } // Udostępnij instancję Json
        single { provideHttpClient(get()) } // Udostępnij HttpClient, wstrzykując Json
    }

// Funkcja tworząca instancję Json (można dostosować)
private fun provideJson(): Json =
    Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true // Ważne, jeśli API zwraca więcej pól niż DTO
    }

// Funkcja tworząca i konfigurująca HttpClient
private fun provideHttpClient(json: Json): HttpClient =
    HttpClient {
        // Ktor dla odpowiedniej platformy zostanie wybrany automatycznie
        install(ContentNegotiation) {
            json(json) // Użyj skonfigurowanej instancji Json
        }
        // Opcjonalnie: Inne pluginy Ktor (np. Auth, defaultRequest)
        // install(Auth) { ... }
        // defaultRequest { ... }
    }
