package io2.hobbymatch.hobby.data

import io2.hobbymatch.hobby.data.remote.HobbyApiService

class HobbyRepository(
    private val apiService: HobbyApiService,
) {
    suspend fun getHobbies() = apiService.getHobbies()
}