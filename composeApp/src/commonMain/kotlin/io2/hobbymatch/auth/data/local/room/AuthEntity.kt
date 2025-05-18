package io2.hobbymatch.auth.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_data")
data class AuthEntity(
    @PrimaryKey
    val id: String = "LOGIN_DATA",
    val role: String = "",
    val token: String = "", // Dodane pole dla kompatybilności z poprzednimi metodami
    val accessToken: String = "",
    val refreshToken: String = "",
    val userId: Long = 0,
    val email: String = "",
    val name: String = ""
)