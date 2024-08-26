package com.onthecrow.caffeine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.onthecrow.caffeine.data.SettingsDataStore
import com.onthecrow.caffeine.service.caffeine.CaffeineServiceConnectionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AutoStartReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settings: SettingsDataStore

    @Inject
    lateinit var caffeineServiceConnectionManager: CaffeineServiceConnectionManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val settings = runBlocking { settings.settings.firstOrNull() }
            if (settings?.isStarted == true && settings.isRebootPersistent) {
                caffeineServiceConnectionManager.start()
            }
        }
    }
}