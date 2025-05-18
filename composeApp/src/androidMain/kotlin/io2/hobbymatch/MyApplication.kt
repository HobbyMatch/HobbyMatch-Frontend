package io2.hobbymatch

import android.app.Application
import io2.hobbymatch.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidContext(
                this@MyApplication
            )
        }
    }
}