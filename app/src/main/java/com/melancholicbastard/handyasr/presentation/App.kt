package com.melancholicbastard.handyasr.presentation

import android.app.Application
import com.melancholicbastard.handyasr.presentation.di.AppContainer

class App : Application() {
    val appContainer: AppContainer by lazy { AppContainer() }

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}

