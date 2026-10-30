package com.onthecrow.caffeine.data.permission

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import com.onthecrow.caffeine.R
import com.onthecrow.caffeine.service.tile.CaffeineTileService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Quick Settings tile (Android 13+). There is no public API to query whether the user
 * already added the tile, so the onboarding slide is button-only and never reports
 * "granted".
 *
 * [request] asks the system to add the tile; it requires the app to be in the foreground,
 * which holds while the onboarding dialog is on screen.
 */
class QuickTileManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionManager {

    override val isGranted: Flow<Boolean> = flowOf(false)

    override fun request() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        addTile()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun addTile() {
        context.getSystemService(StatusBarManager::class.java).requestAddTileService(
            ComponentName(context, CaffeineTileService::class.java),
            context.getString(R.string.app_name),
            Icon.createWithResource(context, R.drawable.ic_coffee_bean),
            { it.run() },
            { /* result code ignored — no API to persist tile state */ },
        )
    }
}
