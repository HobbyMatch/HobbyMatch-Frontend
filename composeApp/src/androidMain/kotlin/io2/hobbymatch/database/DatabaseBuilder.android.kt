package io2.hobbymatch.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io2.hobbymatch.auth.data.local.room.AuthDatabase
import io2.hobbymatch.user.data.local.room.UserDatabase

fun getAuthDatabaseBuilder(context: Context):  RoomDatabase.Builder<AuthDatabase> {
    val dbFile = context.getDatabasePath("auth.db")
    return Room.databaseBuilder(
        context = context,
        name = dbFile.absolutePath)
}

fun getUserDatabaseBuilder(context: Context):  RoomDatabase.Builder<UserDatabase> {
    val dbFile = context.getDatabasePath("user.db")
    return Room.databaseBuilder(
        context = context,
        name = dbFile.absolutePath)
}