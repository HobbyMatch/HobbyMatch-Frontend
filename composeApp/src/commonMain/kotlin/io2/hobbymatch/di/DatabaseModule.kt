package io2.hobbymatch.di

import io2.hobbymatch.login.data.local.realm.LoginMongoDB
import io2.hobbymatch.user.data.local.realm.UserMongoDB
import org.koin.dsl.module

val databaseModule = module {
    println("Initializing databaseModule...")
    single { LoginMongoDB() }
    single { UserMongoDB() }
}