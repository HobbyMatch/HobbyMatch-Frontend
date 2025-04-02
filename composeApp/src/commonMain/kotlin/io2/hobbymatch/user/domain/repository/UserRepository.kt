package io2.hobbymatch.user.domain.repository

import io2.hobbymatch.user.data.local.realm.UserProfileRealm // Lub model domenowy User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Obserwuj profil użytkownika (prawdopodobnie z lokalnej bazy danych)
    fun getUserProfileFlow(): Flow<UserProfileRealm?> // Zwraca null, jeśli nie ma profilu

    // Zapisz profil (lokalnie i zdalnie)
    suspend fun saveUserProfile(profile: UserProfileRealm) // Przyjmuje obiekt Realm lub domenowy

    // Możesz dodać inne metody, np. do pobrania ID użytkownika
    // fun getCurrentUserId(): String?
}