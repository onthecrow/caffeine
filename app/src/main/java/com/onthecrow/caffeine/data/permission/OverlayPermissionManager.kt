package com.onthecrow.caffeine.data.permission

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.onthecrow.caffeine.CaffeineApplication
import com.onthecrow.caffeine.core.ActivityResumedCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class OverlayPermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionManager {

    private val _isGranted: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)
    override val isGranted: Flow<Boolean> get() = _isGranted

    init {
        (context as CaffeineApplication).registerActivityLifecycleCallbacks(
            ActivityResumedCallback { _isGranted.tryEmit(Settings.canDrawOverlays(context)) }
        )
        _isGranted.tryEmit(Settings.canDrawOverlays(context))
    }

    override fun request() {
        context.startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}