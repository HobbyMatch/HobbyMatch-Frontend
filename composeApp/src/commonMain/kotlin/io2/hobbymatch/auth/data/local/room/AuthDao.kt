package io2.hobbymatch.auth.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAuth(auth: AuthEntity)

    @Query("SELECT * FROM auth_data WHERE id = 'LOGIN_DATA' LIMIT 1")
    suspend fun getAuthData(): AuthEntity?

    @Query("SELECT * FROM auth_data WHERE id = 'LOGIN_DATA' LIMIT 1")
    fun getAuthDataFlow(): Flow<AuthEntity?>

    @Query("DELETE FROM auth_data WHERE id = 'LOGIN_DATA'")
    suspend fun deleteAuthData()

    // New methods to match AuthMongoDB functionality

    // For token operations
    @Query("UPDATE auth_data SET accessToken = :accessToken WHERE id = 'LOGIN_DATA'")
    suspend fun saveLoginToken(accessToken: String)

    @Query("SELECT accessToken FROM auth_data WHERE id = 'LOGIN_DATA' LIMIT 1")
    suspend fun loadLoginToken(): String?

    @Query("SELECT accessToken FROM auth_data WHERE id = 'LOGIN_DATA' LIMIT 1")
    fun getLoginTokenFlow(): Flow<String?>

    // For role operations
    @Query("UPDATE auth_data SET role = :role WHERE id = 'LOGIN_DATA'")
    suspend fun saveRole(role: String)

    @Query("SELECT role FROM auth_data WHERE id = 'LOGIN_DATA' LIMIT 1")
    suspend fun loadRole(): String?

    // For auth response operations
    @Query("SELECT * FROM auth_data WHERE id = 'LOGIN_DATA' LIMIT 1")
    suspend fun loadAuthResponse(): AuthEntity?

    // Combined save operation for auth response and role
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAuthResponseWithRole(auth: AuthEntity)
}