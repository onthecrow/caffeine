package com.onthecrow.caffeine.ui.main

import com.onthecrow.caffeine.data.CaffeineSettings

data class MainState(
    val isActive: Boolean = false,
    val caffeineSettings: CaffeineSettings = CaffeineSettings.DEFAULT,
)