package com.onthecrow.caffeine.ui.main

import com.onthecrow.caffeine.data.CaffeineSettings

data class MainState(
    val isActive: Boolean = false,
    val isEasterAnimationRunning: Boolean = false,
    val caffeineSettings: CaffeineSettings = CaffeineSettings.DEFAULT,
    val onboardingDialogState: OnboardingDialogState = OnboardingDialogState(),
)

data class OnboardingDialogState(
    val isShowing: Boolean = false
)