package io2.hobbymatch.hobby.data.remote

import io2.hobbymatch.hobby.domain.Hobby

class MockHobbyApiService : HobbyApiService {
    override suspend fun getHobbies(): List<Hobby> {
        return listOf(
            Hobby(name = "Basketball"),
            Hobby(name = "Chess"),
            Hobby(name = "Fishing"),
            Hobby(name = "Football"),
            Hobby(name = "Gym"),
            Hobby(name = "Hiking"),
            Hobby(name = "Running"),
            Hobby(name = "Swimming"),
            Hobby(name = "Tennis")
        )
    }
}