package io2.hobbymatch.auth.data.local.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [AuthEntity::class],
    version = 2,
    exportSchema = true)
@ConstructedBy(AuthDatabaseConstructor::class)
abstract class AuthDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AuthDatabaseConstructor : RoomDatabaseConstructor<AuthDatabase> {
    override fun initialize(): AuthDatabase
}