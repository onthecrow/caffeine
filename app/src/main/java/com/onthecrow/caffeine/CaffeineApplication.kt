package com.onthecrow.caffeine

import android.app.Application
import com.onthecrow.caffeine.core.logger.FileLogger
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class CaffeineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FileLogger.initialize(this)
    }
}