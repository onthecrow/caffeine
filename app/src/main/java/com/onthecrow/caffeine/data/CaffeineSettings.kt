package com.onthecrow.caffeine.data

import androidx.compose.runtime.Stable
import com.onthecrow.caffeine.core.PersistentListSerializer
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.concurrent.TimeUnit

@Stable
@Serializable
data class CaffeineSettings(
    @Stable
    @Serializable(with = PersistentListSerializer::class)
    val timerOptions: PersistentList<SettingsTimerOption>,
    val isPersistent: Boolean,
    val isRebootPersistent: Boolean,
    val isAutomaticallyTurnOff: Boolean,
    val isStarted: Boolean,
) {
    companion object {
        val DEFAULT get() = CaffeineSettings(
            timerOptions = persistentListOf(
                SettingsTimerOption.Indefinite(),
                SettingsTimerOption.Finite(TimeUnit.SECONDS.toMillis(15)),
                SettingsTimerOption.Finite(TimeUnit.SECONDS.toMillis(30)),
                SettingsTimerOption.Finite(TimeUnit.SECONDS.toMillis(60)),
            ),
            isPersistent = true,
            isRebootPersistent = true,
            isAutomaticallyTurnOff = false,
            isStarted = false,
        )
    }
}

@Stable
@Serializable
sealed class SettingsTimerOption(
    @SerialName("durationMillis")
    open val durationMillis: Long,
    @SerialName("isActive")
    open val isActive: Boolean = false,
) {
    @Serializable
    data class Indefinite(@Transient override val isActive: Boolean = true): SettingsTimerOption(-1, isActive)
    @Serializable
    data class Custom(@Transient override val durationMillis: Long = -1): SettingsTimerOption(durationMillis)
    @Serializable
    data class Finite(@Transient override val durationMillis: Long = -1): SettingsTimerOption(durationMillis)
}
