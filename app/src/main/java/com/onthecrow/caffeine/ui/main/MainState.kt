package com.onthecrow.caffeine.ui.main

import androidx.compose.runtime.Stable
import com.onthecrow.caffeine.data.CaffeineSettings

@Stable
data class MainState(
    val isActive: Boolean,
    val caffeineSettings: CaffeineSettings,
)