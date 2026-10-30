package com.onthecrow.caffeine.data.permission

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import com.onthecrow.caffeine.CaffeineApplication
import com.onthecrow.caffeine.core.ActivityResumedCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

/**
 * "Ignore battery optimizations" exemption. Helps the foreground service and the boot
 * auto-start survive on aggressive OEM battery managers (Xiaomi, Huawei, etc.).
 *
 * Re-checks on every activity resume so returning from the system dialog updates the state.
 */
class BatteryOptimizationManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionManager {

    private val _isGranted: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)
    override val isGranted: Flow<Boolean> get() = _isGranted

    private val powerManager: PowerManager
        get() = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    private fun currentlyGranted(): Boolean =
        powerManager.isIgnoringBatteryOptimizations(context.packageName)

    init {
        (context as CaffeineApplication).registerActivityLifecycleCallbacks(
            ActivityResumedCallback { _isGranted.tryEmit(currentlyGranted()) }
        )
        _isGranted.tryEmit(currentlyGranted())
    }

    override fun request() {
        // Open this app's own settings page (reliable on every OEM). From there the battery /
        // "background activity" control is one tap away — on OnePlus/ColorOS that's the per-app
        // "Power consumption control" screen, which the generic battery-optimization list can't open.
        // Falls back to the battery-optimization list, then plain Settings, if unavailable.
        val intents = listOf(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null),
            ),
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS),
            Intent(Settings.ACTION_SETTINGS),
        )
        for (intent in intents) {
            try {
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return
            } catch (_: ActivityNotFoundException) {
                // Try the next fallback.
            }
        }
    }
}
