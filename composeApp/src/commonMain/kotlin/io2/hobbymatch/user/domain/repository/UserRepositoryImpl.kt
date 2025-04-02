package io2.hobbymatch.user.domain.repository

import io2.hobbymatch.user.data.local.realm.UserMongoDB
import io2.hobbymatch.user.data.local.realm.UserProfileRealm
import io2.hobbymatch.user.data.remote.UserApiService
import io2.hobbymatch.user.data.remote.dto.UserProfileDto
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val userApiService: UserApiService,
    private val userMongoDB: UserMongoDB // Wstrzyknij UserMongoDB
) : UserRepository {

    override fun getUserProfileFlow(): Flow<UserProfileRealm?> {
        // Pobieraj dane z lokalnej bazy danych
        return userMongoDB.getUserProfileFlow()
    }

    override suspend fun saveUserProfile(profile: UserProfileRealm) {
        // 1. Zapisz lokalnie
        userMongoDB.saveUserProfile(profile)

        // 2. Spróbuj zapisać zdalnie (TODO: Potrzebny userId)
        val userId = "TODO-GET-USER-ID" // Skąd wziąć ID zalogowanego użytkownika? Może z tokena?
        try {
            // TODO: Mapowanie UserProfileRealm na UserProfileDto
            val profileDto = UserProfileDto(
                email = profile.email,
                username = profile.username,
                name = profile.name,
                surname = profile.surname,
                hobbies = profile.hobbies.toList(),
                birthday = profile.birthday,
                gender = profile.gender,
                bio = profile.bio
            )
            userApiService.updateUserProfile(userId, profileDto)
        } catch (e: Exception) {
            // TODO: Co zrobić, gdy zapis zdalny się nie powiedzie?
            // Logowanie błędu, mechanizm synchronizacji?
            println("Failed to sync user profile update to server: ${e.message}")
        }
    }
}