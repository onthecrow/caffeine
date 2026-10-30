package com.onthecrow.caffeine

import android.app.Application
import com.onthecrow.caffeine.core.logger.FileLogger
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class CaffeineApplication : Application() {

    @Inject lateinit var permissionRequester: PermissionRequester

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            FileLogger.initialize(this)
            Timber.plant(Timber.DebugTree())
        }
    }
}