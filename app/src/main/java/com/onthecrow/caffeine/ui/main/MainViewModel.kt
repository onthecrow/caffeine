package com.onthecrow.caffeine.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthecrow.caffeine.data.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
): ViewModel() {

    private val isActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

    // todo separate UI state and datastore, add debounce
    val state: StateFlow<MainState> get() = combine(
        isActive,
        settingsDataStore.settings,
    ) { isActive, settings ->
        MainState(isActive, settings)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MainState())

    fun setIsPersistent(isPersistent: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateSettings(
                settingsDataStore.settings.first().copy(isPersistent = isPersistent)
            )
        }
    }

    fun setIsRebootPersistent(isRebootPersistent: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateSettings(
                settingsDataStore.settings.first().copy(isRebootPersistent = isRebootPersistent)
            )
        }
    }

    fun setIsAutomaticTurnOff(isAutomaticTurnOff: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateSettings(
                settingsDataStore.settings.first().copy(isAutomaticallyTurnOff = isAutomaticTurnOff)
            )
        }
    }
}