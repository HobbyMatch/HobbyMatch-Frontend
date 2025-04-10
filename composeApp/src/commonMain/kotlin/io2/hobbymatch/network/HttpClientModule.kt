package io2.hobbymatch.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single { provideJson() }
    single { provideHttpClient(get()) }
    // Add LoginApiService to DI
    single { LoginApiService(get()) }
}

private fun provideJson(): Json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

// Create a TokenManager object to hold the token
object TokenStorage {
    private var currentToken: String? = null

    fun setToken(token: String?) {
        currentToken = token
    }

    fun getToken(): String? = currentToken
}

private fun provideHttpClient(json: Json): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }

    // Add bearer token authentication
    install(Auth) {
        bearer {
            loadTokens {
                // Get token from TokenStorage
                val token = TokenStorage.getToken()
                if (token != null) {
                    BearerTokens(token, token)
                } else {
                    null
                }
            }
        }
    }
}
