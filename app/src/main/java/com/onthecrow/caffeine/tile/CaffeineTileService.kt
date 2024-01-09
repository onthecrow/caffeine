package com.onthecrow.caffeine.tile

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Icon
import android.os.IBinder
import android.service.quicksettings.TileService
import android.util.Log
import androidx.core.content.ContextCompat
import com.onthecrow.caffeine.wakelock.WakeLockService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.Optional
import kotlin.properties.Delegates


class CaffeineTileService : TileService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private val serviceConnection by lazy { initServiceConnection() }
    private var shouldToggle: Boolean = false
    private var tileState by Delegates.observable(CaffeineTileState()) { _, _, _ ->
        requestListeningState(
            applicationContext,
            ComponentName(applicationContext, CaffeineTileService::class.java)
        )
    }
    private var wakeLockStateSubscription: Job? = null
    private var wakelockService = Optional.empty<WakeLockService>()

    override fun onTileRemoved() {
        super.onTileRemoved()
        Log.d(this@CaffeineTileService.javaClass.simpleName, "onTileRemoved()")
        unbindWakeLockService(true)
    }

    override fun onCreate() {
        super.onCreate()
        bindWakeLockService()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(this@CaffeineTileService.javaClass.simpleName, "onDestroy()")
        unbindWakeLockService()
    }

    override fun onStartListening() {
        super.onStartListening()
        Log.d(this@CaffeineTileService.javaClass.simpleName, "onStartListening()")
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        if (wakelockService.isPresent) {
            wakelockService.get().toggle()
            unbindWakeLockService(true)
        } else {
            shouldToggle = true
            bindWakeLockService(true)
        }
        Log.d(
            this@CaffeineTileService.javaClass.simpleName,
            "isPresent: ${wakelockService.isPresent}"
        )
    }

    private fun bindWakeLockService(shouldCreate: Boolean = false) {
        try {
            if (shouldCreate) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, WakeLockService::class.java)
                )
            }
            bindService(
                Intent(applicationContext, WakeLockService::class.java),
                serviceConnection,
                0
            )
        } catch (error: Throwable) {
            tileState = CaffeineTileState()
            Log.e(javaClass.simpleName, error.message, error)
        }
    }

    private fun unbindWakeLockService(shutdownService: Boolean = false) {
        if (wakelockService.isPresent) {
            unbindService(serviceConnection)
            if (shutdownService) {
                stopService(Intent(applicationContext, WakeLockService::class.java))
            }
            wakelockService = Optional.empty()
        }
    }

    private fun updateTileState() {
        with(qsTile) {
            label = getString(tileState.label)
            contentDescription = getString(tileState.contentDescription)
            state = tileState.state
            icon = Icon.createWithResource(applicationContext, tileState.icon)
            updateTile()
        }
        Log.d(
            this.javaClass.simpleName, """
            Tile updated ($tileState)
        """.trimIndent()
        )
    }

    private fun initServiceConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                Log.d(this@CaffeineTileService.javaClass.simpleName, "connected()")
                val service = (iBinder as WakeLockService.Binder).service
                wakelockService = Optional.of(service)
                wakeLockStateSubscription = service.getStateFlow()
                    .map(::mapWakeLockStateToTileState)
                    .onEach { newState -> tileState = newState }
                    .launchIn(coroutineScope)
                if (shouldToggle) {
                    shouldToggle = false
                    wakelockService.get().toggle()
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                Log.d(this@CaffeineTileService.javaClass.simpleName, "disconnected()")
                wakelockService = Optional.empty<WakeLockService>()
                wakeLockStateSubscription?.cancel()
                wakeLockStateSubscription = null
            }
        }
    }
}