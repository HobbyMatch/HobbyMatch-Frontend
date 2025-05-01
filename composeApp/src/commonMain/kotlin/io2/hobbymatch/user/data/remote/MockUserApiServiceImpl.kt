package io2.hobbymatch.user.data.remote

import io2.hobbymatch.user.domain.Hobby
import io2.hobbymatch.user.domain.User
import kotlinx.coroutines.delay

class MockUserApiService : UserApiService {

    // Mock data for testing
    private val mockAuthenticatedUser = User(
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
        delay(500) // Simulate network delay for realistic behavior
        return mockAuthenticatedUser
    }

    override suspend fun getUser(userId: String): User {
        delay(500) // Simulate network delay
        return mockUsers[userId] ?: throw IllegalArgumentException("User with ID $userId not found.")
    }

    override suspend fun updateAuthenticatedUser(user: User) {
        delay(500) // Simulate network delay
        // Mock update: overwrite the authenticated user
        mockAuthenticatedUser.apply {
            name = user.name
            email = user.email
            hobbies = user.hobbies
        }
    }

    override suspend fun updateUser(userId: String, user: User) {
        delay(500) // Simulate network delay
        if (mockUsers.containsKey(userId)) {
            // Update the existing user
            mockUsers[userId] = user
        } else {
            // Add a new user if it doesn't exist
            mockUsers[userId] = user
        }
    }
}