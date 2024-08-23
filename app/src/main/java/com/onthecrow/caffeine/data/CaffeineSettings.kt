package com.onthecrow.caffeine.data

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class CaffeineSettings(
    val isPersistent: Boolean,
    val isRebootPersistent: Boolean,
    val isAutomaticallyTurnOff: Boolean,
    val isStarted: Boolean,
) {
    companion object {
        val DEFAULT get() = CaffeineSettings(
            isPersistent = true,
            isRebootPersistent = true,
            isAutomaticallyTurnOff = false,
            isStarted = false,
        )
    }
}
