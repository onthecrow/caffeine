package com.onthecrow.caffeine.wakelock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScreenStateReceiver {

    val currentScreenState: ScreenState
        get() = screenStateFlow.value

    private val receiver: BroadcastReceiver by lazy(LazyThreadSafetyMode.NONE) { initReceiver() }
    private val screenStateFlow: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState.SCREEN_ON)
    private var screenReceiverJob: Job? = null
    private var screenStateJob: Job? = null

    private lateinit var powerManager: PowerManager

    fun register(context: Context, onScreenStateChanged: (ScreenState) -> Unit) {
        powerManager = context.getSystemService(PowerManager::class.java)
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        context.registerReceiver(receiver, intentFilter)
        screenStateJob = screenStateFlow
            .onEach { state -> onScreenStateChanged(state) }
            .launchIn(MainScope())
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(receiver)
        screenStateJob?.cancel()
        screenStateJob = null
    }

    private fun updateCurrentScreenState() {
        screenStateFlow.update {
            when (powerManager.isInteractive) {
                false -> ScreenState.SCREEN_OFF
                true -> ScreenState.SCREEN_ON
            }
        }
    }

    /**
     * Screen state in PowerManager changes sometimes slower then corresponding broadcast receive.
     * It can leads to inconsistency when user clicks power button fast enough.
     * Double check on every broadcast just to be sure =)
     */
    private fun doubleCheck() {
        screenReceiverJob?.cancel()
        screenReceiverJob = MainScope().launch {
            delay(DELAY_DOUBLE_CHECK)
            updateCurrentScreenState()
        }
    }

    private fun initReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                updateCurrentScreenState()
                doubleCheck()
            }
        }
    }

    companion object {
        private const val DELAY_DOUBLE_CHECK = 1_000L
    }
}

enum class ScreenState {
    SCREEN_ON, SCREEN_OFF
}