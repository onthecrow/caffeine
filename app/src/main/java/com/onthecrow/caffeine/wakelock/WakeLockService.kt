package com.onthecrow.caffeine.wakelock

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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


class WakeLockService : Service() {

    private val wakeLockView by lazy { WakeLockViewWrapper(applicationContext) }
    private val state = MutableSharedFlow<WakeLockServiceState>(STATE_REPLAY_COUNT)

    init {
        updateState(WakeLockServiceState.INACTIVE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.hasExtra(EXTRA_SHUTDOWN) == true) {
            stopSelf()
        } else {
            startForeground()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return Binder()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wakeLockView.isShown()) {
            wakeLockView.remove()
            updateState(WakeLockServiceState.INACTIVE)
        }
    }

    fun getStateFlow(): SharedFlow<WakeLockServiceState> {
        return state
    }

    fun toggle() {
        if (wakeLockView.isShown()) {
            wakeLockView.remove()
            updateState(WakeLockServiceState.INACTIVE)
        } else {
            wakeLockView.show()
            updateState(WakeLockServiceState.ACTIVE)
        }
    }

    private fun updateState(newState: WakeLockServiceState) {
        MainScope().launch {
            state.emit(newState)
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
                    Intent(applicationContext, WakeLockService::class.java).apply {
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

    inner class Binder : android.os.Binder() {
        val service: WakeLockService
            get() = this@WakeLockService
    }

    companion object {
        private const val ID_FOREGROUND_SERVICE = 50561
        private const val ID_NOTIFICATION_CHANNEL = "34591234"
        private const val STATE_REPLAY_COUNT = 1
        private const val REQUEST_CODE_DELETE = 3414352
        private const val EXTRA_SHUTDOWN = "shutdown"
    }
}