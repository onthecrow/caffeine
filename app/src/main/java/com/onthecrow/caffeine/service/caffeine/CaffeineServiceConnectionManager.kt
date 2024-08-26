package com.onthecrow.caffeine.service.caffeine

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.onthecrow.caffeine.core.logger.FileLogger.log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaffeineServiceConnectionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val serviceConnection by lazy { initServiceConnection() }
    private val _serviceState: MutableStateFlow<CaffeineServiceState> = MutableStateFlow(CaffeineServiceState.INACTIVE)
    val serviceState: Flow<CaffeineServiceState> get() = _serviceState

    init {
        bindCaffeineService()
    }

    fun start() {
        if (_serviceState.value != CaffeineServiceState.ACTIVE) {
            bindCaffeineService(true)
        }
    }

    fun stop() {
        context.stopService(Intent(context, CaffeineService::class.java))
    }

    private fun bindCaffeineService(shouldStart: Boolean = false) {
        try {
            if (shouldStart) {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, CaffeineService::class.java)
                )
            }
            context.bindService(
                Intent(context, CaffeineService::class.java),
                serviceConnection,
                0
            )
        } catch (error: Throwable) {
            _serviceState.update {
                CaffeineServiceState.INACTIVE
            }
            Log.e(javaClass.simpleName, error.message, error)
            log(error.toString())
        }
    }

    private fun initServiceConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                log("Service connected")
                _serviceState.update { CaffeineServiceState.ACTIVE }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                _serviceState.update { CaffeineServiceState.INACTIVE }
                log("Service disconnected")
            }
        }
    }
}