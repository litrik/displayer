package com.displayer.android.app

import android.app.Application
import com.displayer.coreModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class DisplayerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DisplayerApp)
            modules(listOf(coreModule))
            androidLogger(Level.INFO)
        }
    }
}