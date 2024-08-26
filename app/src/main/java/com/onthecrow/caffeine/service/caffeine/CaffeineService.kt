package com.onthecrow.caffeine.service.caffeine

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat
import com.onthecrow.caffeine.R
import com.onthecrow.caffeine.core.logger.FileLogger.log
import com.onthecrow.caffeine.data.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CaffeineService : Service() {

    @Inject
    lateinit var settings: SettingsDataStore

    private val wakeLockView by lazy(LazyThreadSafetyMode.NONE) {
        CaffeineViewWrapper(
            applicationContext
        )
    }
    private val screenStateReceiver by lazy(LazyThreadSafetyMode.NONE) { ScreenStateReceiver() }
    private var isCreated: Boolean = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("WakeLockService ${android.os.Process.myPid()}")
        if (!isCreated) {
            isCreated = true
            startForeground()
            startCaffeine()
            screenStateReceiver.register(applicationContext, ::onScreenStateChanged)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return Binder()
    }

    override fun onDestroy() {
        super.onDestroy()
        screenStateReceiver.unregister(applicationContext)
        if (wakeLockView.isShown()) {
            wakeLockView.remove()
        }
    }

    private fun startCaffeine() {
        wakeLockView.show()
        // todo make it depends on settings
        wakeLockView.isPersistent = true
        MainScope().launch {
            val currentSettings = settings.settings.first()
            settings.updateSettings(currentSettings.copy(isStarted = true))
        }
    }

    private fun startForeground() {
        startForeground(ID_FOREGROUND_SERVICE, createNotification())
    }

    private fun createNotification(): Notification {
        createNotificationChannel()
        return NotificationCompat.Builder(applicationContext, ID_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_active)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_subtitle))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.notification_subtitle))
            )
            .setDeleteIntent(
                PendingIntentCompat.getForegroundService(
                    applicationContext,
                    REQUEST_CODE_DELETE,
                    Intent(applicationContext, CaffeineService::class.java).apply {
                        putExtra(EXTRA_SHUTDOWN, true)
                    },
                    PendingIntent.FLAG_ONE_SHOT,
                    false
                )
            )
            .setOngoing(true)
            .setSilent(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(ID_NOTIFICATION_CHANNEL, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun onScreenStateChanged(screenState: ScreenState) {
        MainScope().launch {
            val isAutomaticallyTurnOff =
                settings.settings.firstOrNull()?.isAutomaticallyTurnOff ?: false
            if (screenState == ScreenState.SCREEN_OFF && isAutomaticallyTurnOff) {
                stopSelf()
            }
        }
    }

    // todo doesn't needed
    inner class Binder : android.os.Binder() {
        val service: CaffeineService
            get() = this@CaffeineService
    }

    companion object {
        private const val ID_FOREGROUND_SERVICE = 50561
        private const val ID_NOTIFICATION_CHANNEL = "34591234"
        private const val REQUEST_CODE_DELETE = 3414352
        private const val EXTRA_SHUTDOWN = "shutdown"
    }
}