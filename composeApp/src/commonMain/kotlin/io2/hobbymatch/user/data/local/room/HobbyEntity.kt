package io2.hobbymatch.user.data.local.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "hobbies",
    foreignKeys = [ForeignKey(
        entity = UserProfileEntity::class,
        parentColumns = ["id"],
        childColumns = ["userProfileId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class HobbyEntity(
    @PrimaryKey(autoGenerate = true)
    val hobbyId: Long = 0,
    val name: String = "",
    val userProfileId: String // powiązanie z profilem użytkownika
)