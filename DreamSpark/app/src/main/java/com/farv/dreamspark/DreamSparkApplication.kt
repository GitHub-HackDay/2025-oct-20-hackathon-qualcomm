package com.farv.dreamspark

import android.app.Application
import android.util.Log
import kotlin.system.exitProcess

class DreamSparkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("DreamSparkApplication", "Uncaught exception: ", throwable)
            // Here you could add logic to report the crash to a service
            // For now, we'll just exit the process
            exitProcess(1)
        }
    }
}