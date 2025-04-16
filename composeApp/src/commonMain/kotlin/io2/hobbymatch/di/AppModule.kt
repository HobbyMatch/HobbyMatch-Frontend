package io2.hobbymatch.di

import org.koin.dsl.module

val appModule = module {
    includes(
        databaseModule,
        networkModule,
        loginModule,
    )
}