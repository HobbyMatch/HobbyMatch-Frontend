package io2.hobbymatch.auth.data.local

import io2.hobbymatch.auth.data.local.room.AuthRoomDataSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual object AuthLocalDataSourceFactory : KoinComponent {
    private val authRoomDataSource: AuthRoomDataSource by inject()

    actual fun create(): AuthLocalDataSource = authRoomDataSource
}