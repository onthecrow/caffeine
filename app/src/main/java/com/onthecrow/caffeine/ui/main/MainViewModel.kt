package com.onthecrow.caffeine.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthecrow.caffeine.data.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _state: MutableStateFlow<MainState> =
        MutableStateFlow(MainState(false, runBlocking { settingsDataStore.settings.first() }))
    val state: StateFlow<MainState> get() = _state

    init {
        @Suppress("OPT_IN_USAGE")
        state.debounce(300)
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
}