package com.onthecrow.caffeine.data.permission

import android.content.Context
import android.os.Build
import com.onthecrow.caffeine.CaffeineApplication
import com.onthecrow.caffeine.PermissionRequester
import com.onthecrow.caffeine.core.ActivityResumedCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

/**
 * POST_NOTIFICATIONS runtime permission (Android 13+). On older versions the permission
 * does not exist and is always considered granted. The foreground service still runs when
 * it is denied — this only controls whether the ongoing notification is visible.
 *
 * Re-checks on every activity resume so returning from the system permission dialog or
 * settings updates the state. Delegates the actual request to [PermissionRequester].
 */
class NotificationPermissionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionRequester: PermissionRequester,
) : PermissionManager {

    private val _isGranted: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)
    override val isGranted: Flow<Boolean> get() = _isGranted

    private fun currentlyGranted(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || permissionRequester.isGranted

    init {
        (context as CaffeineApplication).registerActivityLifecycleCallbacks(
            ActivityResumedCallback { _isGranted.tryEmit(currentlyGranted()) }
        )
        _isGranted.tryEmit(currentlyGranted())
    }

    override fun request() {
        if (currentlyGranted()) return
        permissionRequester.request { _isGranted.tryEmit(currentlyGranted()) }
    }
}
