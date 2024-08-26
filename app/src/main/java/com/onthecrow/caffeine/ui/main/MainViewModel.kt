package com.onthecrow.caffeine.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthecrow.caffeine.data.SettingsDataStore
import com.onthecrow.caffeine.service.caffeine.CaffeineServiceConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val caffeineServiceConnectionManager: CaffeineServiceConnectionManager,
) : ViewModel() {

    private var tapsCount: Int = 0
    private var easterCounterRebootJob: Job? = null
    private var easterTurnOffJob: Job? = null

    private val _state: MutableStateFlow<MainState> =
        MutableStateFlow(MainState(caffeineSettings = runBlocking { settingsDataStore.settings.first() }))
    val state: StateFlow<MainState> get() = _state

    init {
        @Suppress("OPT_IN_USAGE")
        state.debounce(DEBOUNCE_SETTINGS_APPLY)
            .onEach { settingsDataStore.updateSettings(it.caffeineSettings) }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun setIsPersistent(isPersistent: Boolean) {
        _state.update {
            it.copy(caffeineSettings = it.caffeineSettings.copy(isPersistent = isPersistent))
        }
    }

    fun setIsRebootPersistent(isRebootPersistent: Boolean) {
        _state.update {
            it.copy(caffeineSettings = it.caffeineSettings.copy(isRebootPersistent = isRebootPersistent))
        }
    }

    fun setIsAutomaticTurnOff(isAutomaticTurnOff: Boolean) {
        _state.update {
            it.copy(caffeineSettings = it.caffeineSettings.copy(isAutomaticallyTurnOff = isAutomaticTurnOff))
        }
    }

    fun onHeaderTap() {
        if (state.value.isEasterAnimationRunning) return
        if (tapsCount >= EASTER_TAPS_TO_ANIMATION) {
            _state.update { it.copy(isEasterAnimationRunning = true) }
            easterTurnOffJob?.cancel()
            easterCounterRebootJob = viewModelScope.launch {
                delay(DELAY_EASTER_ANIMATION_REBOOT)
                _state.update { it.copy(isEasterAnimationRunning = false) }
            }
        } else {
            tapsCount += 1
            easterCounterRebootJob?.cancel()
            easterCounterRebootJob = viewModelScope.launch {
                delay(DELAY_EASTER_TAP_COUNTER)
                tapsCount = 0
            }
        }
    }

    fun onRunButtonClick(shouldRun: Boolean) {
        if (shouldRun) {
            caffeineServiceConnectionManager.start()
        } else {
            caffeineServiceConnectionManager.stop()
        }
    }

    companion object {
        private const val DEBOUNCE_SETTINGS_APPLY = 300L
        private const val EASTER_TAPS_TO_ANIMATION = 4
        private const val DELAY_EASTER_TAP_COUNTER = 500L
        private const val DELAY_EASTER_ANIMATION_REBOOT = 11000L
    }
}