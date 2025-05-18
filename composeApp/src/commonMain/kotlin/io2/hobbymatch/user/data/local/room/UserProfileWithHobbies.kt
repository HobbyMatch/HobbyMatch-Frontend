package io2.hobbymatch.user.data.local.room

import androidx.room.Embedded
import androidx.room.Relation

data class UserProfileWithHobbies(
    @Embedded val userProfile: UserProfileEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userProfileId"
    )
    val hobbies: List<HobbyEntity>
)