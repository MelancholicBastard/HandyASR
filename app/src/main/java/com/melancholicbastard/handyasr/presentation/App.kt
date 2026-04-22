package com.melancholicbastard.handyasr.presentation

import android.app.Application
import com.melancholicbastard.handyasr.presentation.di.AppContainer
import java.io.File

class App : Application() {
    val appContainer: AppContainer by lazy { AppContainer(applicationContext) }

    companion object {
        lateinit var instance: App
            private set
        lateinit var recordingsDir: File
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        recordingsDir = File(filesDir, "recordings")
        if (!recordingsDir.exists()) {
            recordingsDir.mkdirs()
        }
    }
}

