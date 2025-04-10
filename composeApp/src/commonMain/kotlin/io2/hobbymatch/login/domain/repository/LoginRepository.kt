package io2.hobbymatch.login.domain.repository

import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    // Waliduj token z backendem (zwraca np. true/false lub obiekt z danymi usera)
    suspend fun validateTokenWithBackend(idToken: String): Result<Boolean> // Prostsza wersja, true=sukces

    // Operacje na lokalnym tokenie
    suspend fun saveTokenLocally(token: String)

    suspend fun loadTokenLocally(): String?

    fun getLocalTokenFlow(): Flow<String?>

    suspend fun clearLocalToken()
}
