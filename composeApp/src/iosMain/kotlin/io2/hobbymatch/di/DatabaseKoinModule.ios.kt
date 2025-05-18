package io2.hobbymatch.di

import io2.hobbymatch.database.getDatabaseBuilder
import org.koin.dsl.module

actual val targetDatabaseModule = module {
    single { getDatabaseBuilder() }
}