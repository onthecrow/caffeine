package com.onthecrow.caffeine.ui.main

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthecrow.caffeine.data.SettingsDataStore
import com.onthecrow.caffeine.data.permission.BatteryOptimizationManager
import com.onthecrow.caffeine.data.permission.NotificationPermissionManager
import com.onthecrow.caffeine.data.permission.OnboardingPermission
import com.onthecrow.caffeine.data.permission.OverlayPermissionManager
import com.onthecrow.caffeine.data.permission.PermissionManager
import com.onthecrow.caffeine.data.permission.QuickTileManager
import com.onthecrow.caffeine.service.caffeine.CaffeineServiceConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private val overlayPermissionManager: OverlayPermissionManager,
    private val notificationPermissionManager: NotificationPermissionManager,
    private val batteryOptimizationManager: BatteryOptimizationManager,
    private val quickTileManager: QuickTileManager,
) : ViewModel() {

    private var tapsCount: Int = 0
    private var easterCounterRebootJob: Job? = null
    private var easterTurnOffJob: Job? = null

    private val isTiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    /**
     * Detectable steps whose granted state drives the slides, ordered by descending
     * criticality. Notifications are only relevant on Android 13+; the tile step is
     * appended separately as it has no queryable granted state.
     */
    private val flowPermissions: List<OnboardingPermission> = buildList {
        add(OnboardingPermission.OVERLAY)
        if (isTiramisu) add(OnboardingPermission.NOTIFICATIONS)
        add(OnboardingPermission.BATTERY_OPTIMIZATION)
    }

    private val managerFor: Map<OnboardingPermission, PermissionManager> = mapOf(
        OnboardingPermission.OVERLAY to overlayPermissionManager,
        OnboardingPermission.NOTIFICATIONS to notificationPermissionManager,
        OnboardingPermission.BATTERY_OPTIMIZATION to batteryOptimizationManager,
        OnboardingPermission.QUICK_TILE to quickTileManager,
    )

    /** Guards the startup auto-open so it fires once per ViewModel lifecycle. */
    private var didAutoOpen = false

    private val _state: MutableStateFlow<MainState> =
        MutableStateFlow(MainState(caffeineSettings = runBlocking { settingsDataStore.settings.first() }))
    val state: StateFlow<MainState> get() = _state

    init {
        @Suppress("OPT_IN_USAGE")
        state.debounce(DEBOUNCE_SETTINGS_APPLY)
            .onEach { settingsDataStore.updateSettings(it.caffeineSettings) }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        combine(flowPermissions.map { managerFor.getValue(it).isGranted }) { granted ->
            buildList {
                flowPermissions.forEachIndexed { index, permission ->
                    add(OnboardingSlideState(permission, granted[index]))
                }
                if (isTiramisu) add(OnboardingSlideState(OnboardingPermission.QUICK_TILE, isGranted = false))
            }
        }
            .onEach { slides -> updateOnboarding(slides) }
            .launchIn(viewModelScope)
    }

    private fun updateOnboarding(slides: List<OnboardingSlideState>) {
        val overlayMissing = slides.any {
            it.permission == OnboardingPermission.OVERLAY && !it.isGranted
        }
        // First ungranted step, ignoring the always-button tile step.
        val firstUngranted = slides.indexOfFirst {
            !it.isGranted && it.permission != OnboardingPermission.QUICK_TILE
        }.coerceAtLeast(0)

        _state.update { st ->
            val onboarding = st.onboardingDialogState
            val autoOpenNow = overlayMissing && !didAutoOpen
            if (autoOpenNow) didAutoOpen = true
            st.copy(
                onboardingDialogState = onboarding.copy(
                    slides = slides,
                    // Freeze the page while the dialog is open so the user isn't yanked around.
                    initialPage = if (onboarding.isShowing) onboarding.initialPage else firstUngranted,
                    isShowing = onboarding.isShowing || autoOpenNow,
                )
            )
        }
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

    fun onOnboardingButtonClick() {
        _state.update {
            val onboardingState = it.onboardingDialogState
            it.copy(onboardingDialogState = onboardingState.copy(isShowing = !onboardingState.isShowing))
        }
    }

    fun onRequestPermission(permission: OnboardingPermission) {
        managerFor.getValue(permission).request()
    }

    companion object {
        private const val DEBOUNCE_SETTINGS_APPLY = 300L
        private const val EASTER_TAPS_TO_ANIMATION = 4
        private const val DELAY_EASTER_TAP_COUNTER = 500L
        private const val DELAY_EASTER_ANIMATION_REBOOT = 11000L
    }
}
