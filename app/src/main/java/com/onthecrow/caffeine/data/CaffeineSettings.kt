package com.onthecrow.caffeine.data

import com.onthecrow.caffeine.core.PersistentListSerializer
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class CaffeineSettings(
    @Serializable(with = PersistentListSerializer::class)
    val timerOptions: PersistentList<SettingsTimerOption>,
    val isAggressive: Boolean,
    val isAutomaticallyTurnOff: Boolean,
) {
    companion object {
        val DEFAULT get() = CaffeineSettings(
            timerOptions = persistentListOf(
                SettingsTimerOption(1, true),
                SettingsTimerOption(2, true),
                SettingsTimerOption(3, true),
                SettingsTimerOption(4, true),
                SettingsTimerOption(5, true),
            ),
            isAggressive = true,
            isAutomaticallyTurnOff = false,
        )
    }
}

@Serializable
data class SettingsTimerOption(
    val id: Int,
    val isActive: Boolean,
)
