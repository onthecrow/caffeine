package com.onthecrow.caffeine.ui.main

import com.onthecrow.caffeine.data.CaffeineSettings
import com.onthecrow.caffeine.data.permission.OnboardingPermission

data class MainState(
    val isActive: Boolean = false,
    val isEasterAnimationRunning: Boolean = false,
    val caffeineSettings: CaffeineSettings = CaffeineSettings.DEFAULT,
    val onboardingDialogState: OnboardingDialogState = OnboardingDialogState(),
)

data class OnboardingDialogState(
    val isShowing: Boolean = false,
    /** Index of the first ungranted slide, used to position the pager when the dialog opens. */
    val initialPage: Int = 0,
    val slides: List<OnboardingSlideState> = emptyList(),
)

data class OnboardingSlideState(
    val permission: OnboardingPermission,
    val isGranted: Boolean = false,
)
