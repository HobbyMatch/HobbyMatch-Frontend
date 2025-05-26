package io2.hobbymatch.hobby.data.remote

import io2.hobbymatch.hobby.domain.Hobby

interface HobbyApiService {
    suspend fun getHobbies(): List<Hobby>
}