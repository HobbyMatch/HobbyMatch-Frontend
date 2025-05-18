package io2.hobbymatch.user.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Transaction
    @Query("SELECT * FROM user_profile WHERE id = :id")
    suspend fun getUserProfileWithHobbies(id: String = "SINGLE_USER_PROFILE"): UserProfileWithHobbies?

    @Transaction
    @Query("SELECT * FROM user_profile WHERE id = :id")
    fun getUserProfileWithHobbiesFlow(id: String = "SINGLE_USER_PROFILE"): Flow<UserProfileWithHobbies?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHobbies(hobbies: List<HobbyEntity>)

    @Query("DELETE FROM hobbies WHERE userProfileId = :userProfileId")
    suspend fun deleteHobbiesForProfile(userProfileId: String)

    @Transaction
    suspend fun saveUserProfileWithHobbies(userProfile: UserProfileEntity, hobbies: List<HobbyEntity>) {
        insertUserProfile(userProfile)
        deleteHobbiesForProfile(userProfile.id)
        insertHobbies(hobbies)
    }
}