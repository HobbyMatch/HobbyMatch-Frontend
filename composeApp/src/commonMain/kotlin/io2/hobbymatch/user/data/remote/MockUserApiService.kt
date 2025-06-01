package io2.hobbymatch.user.data.remote

import io2.hobbymatch.hobby.domain.Hobby
import io2.hobbymatch.user.domain.UpdateUserRequest
import io2.hobbymatch.user.domain.User
import kotlinx.coroutines.delay

class MockUserApiService : UserApiService {

    private var mockAuthenticatedUser = User(
        id = "1",
        name = "John Doe",
        email = "john.doe@example.com",
        hobbies = listOf(Hobby("Reading"), Hobby("Cycling"))
    )

    private val mockUsers = mutableMapOf(
        "1" to User(
            id = "1",
            name = "John Doe",
            email = "john.doe@example.com",
            hobbies = listOf(Hobby("Reading"), Hobby("Cycling"))
        ),
        "2" to User(
            id = "2",
            name = "Jane Smith",
            email = "jane.smith@example.com",
            hobbies = listOf(Hobby("Painting"), Hobby("Music"))
        ),
        "3" to User(
            id = "3",
            name = "Mike Brown",
            email = "mike.brown@example.com",
            hobbies = listOf(Hobby("Gaming"), Hobby("Running"))
        )
    )

    override suspend fun getAuthenticatedUser(accessToken: String): User {
        delay(500) // Simulate network delay
        return mockAuthenticatedUser
    }

    override suspend fun getUserById(userId: String, token: String?): User {
        delay(500) // Simulate network delay
        return mockUsers[userId] ?: throw IllegalArgumentException("User with ID $userId not found.")
    }

    override suspend fun updateAuthenticatedUser(requestBody: UpdateUserRequest): User {
        delay(500) // Simulate network delay
        mockAuthenticatedUser = mockAuthenticatedUser.copy(
            name = requestBody.name,
            email = requestBody.email,
            hobbies = requestBody.hobbies
        )
        return mockAuthenticatedUser
    }

    override suspend fun updateUserById(userId: String, requestBody: UpdateUserRequest, token: String?): User {
        delay(500) // Simulate network delay
        val updatedUser = mockUsers[userId]?.copy(
            name = requestBody.name,
            email = requestBody.email,
            hobbies = requestBody.hobbies
        ) ?: User(
            id = userId,
            name = requestBody.name,
            email = requestBody.email,
            hobbies = requestBody.hobbies
        )
        mockUsers[userId] = updatedUser
        return updatedUser
    }
}