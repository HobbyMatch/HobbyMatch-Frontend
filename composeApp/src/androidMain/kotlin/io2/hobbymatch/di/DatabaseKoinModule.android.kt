package io2.hobbymatch.di

import io2.hobbymatch.database.getAuthDatabaseBuilder
import io2.hobbymatch.database.getUserDatabaseBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val targetDatabaseModule = module {
    single(named("AuthBuilder")) { getAuthDatabaseBuilder(context = get()) }
    single(named("UserBuilder")) { getUserDatabaseBuilder(context = get()) }
}