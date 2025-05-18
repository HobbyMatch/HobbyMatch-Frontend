package io2.hobbymatch.user.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "SINGLE_USER_PROFILE",
    val name: String = "",
    val email: String = ""
)