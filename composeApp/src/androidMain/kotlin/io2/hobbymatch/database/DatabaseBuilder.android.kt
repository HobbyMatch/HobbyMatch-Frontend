package io2.hobbymatch.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io2.hobbymatch.auth.data.local.room.AuthDatabase

fun getDatabaseBuilder(context: Context):  RoomDatabase.Builder<AuthDatabase> {
    val dbFile = context.getDatabasePath("auth.db")
    return Room.databaseBuilder(
        context = context,
        name = dbFile.absolutePath)
}