package io2.hobbymatch.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single { provideJson() } // Udostępnij instancję Json
    single { provideHttpClient(get()) } // Udostępnij HttpClient, wstrzykując Json
}

private fun provideJson(): Json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true // Ważne, jeśli API zwraca więcej pól niż DTO
}

private fun provideHttpClient(json: Json): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }
}