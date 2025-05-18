package io2.hobbymatch.database

import androidx.room.Room
import androidx.room.RoomDatabase
import io2.hobbymatch.auth.data.local.room.AuthDatabase
import io2.hobbymatch.user.data.local.room.UserDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getAuthDatabaseBuilder(): RoomDatabase.Builder<AuthDatabase> {
    val dbFile = documentDirectory() + "/auth.db"
    return Room.databaseBuilder(name = dbFile)
}

fun getUserDatabaseBuilder(): RoomDatabase.Builder<UserDatabase> {
    val dbFile = documentDirectory() + "/user.db"
    return Room.databaseBuilder(name = dbFile)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}