package com.onthecrow.caffeine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.onthecrow.caffeine.data.SettingsDataStore
import com.onthecrow.caffeine.wakelock.WakeLockService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AutoStartReceiver: BroadcastReceiver() {

    @Inject
    lateinit var settings: SettingsDataStore

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val settings = runBlocking { settings.settings.firstOrNull() }
            if (settings?.isStarted == true && settings.isRebootPersistent) {
                context?.run {
                    ContextCompat.startForegroundService(
                        applicationContext,
                        Intent(applicationContext, WakeLockService::class.java)
                    )
                }
            }
        }
    }
}