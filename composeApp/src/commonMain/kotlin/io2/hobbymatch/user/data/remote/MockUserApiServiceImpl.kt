package io2.hobbymatch.user.data.remote

import io2.hobbymatch.user.domain.Hobby
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
        "user123" to User(
            id = "2",
            name = "Jane Smith",
            email = "jane.smith@example.com",
            hobbies = listOf(Hobby("Painting"), Hobby("Music"))
        ),
        "user456" to User(
            id = "3",
            name = "Mike Brown",
            email = "mike.brown@example.com",
            hobbies = listOf(Hobby("Gaming"), Hobby("Running"))
        )
    )

    override suspend fun getAuthenticatedUser(): User {
        delay(500) // Simulate network delay
        return mockAuthenticatedUser
    }

    override suspend fun getUser(userId: String): User {
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

    override suspend fun updateUser(userId: String, requestBody: UpdateUserRequest): User {
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