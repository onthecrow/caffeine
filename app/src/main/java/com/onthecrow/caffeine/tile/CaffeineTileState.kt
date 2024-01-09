package com.onthecrow.caffeine.tile

import android.service.quicksettings.Tile
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.onthecrow.caffeine.R
import com.onthecrow.caffeine.wakelock.WakeLockServiceState

data class CaffeineTileState(
    val state: Int = Tile.STATE_INACTIVE,
    @StringRes val label: Int = R.string.app_name,
    @StringRes val contentDescription: Int = R.string.tile_content_description,
    @DrawableRes val icon: Int = R.drawable.ic_inactive,
)

fun mapWakeLockStateToTileState(wakeLockServiceState: WakeLockServiceState): CaffeineTileState {
    return CaffeineTileState(
        state = when (wakeLockServiceState) {
            WakeLockServiceState.INACTIVE -> Tile.STATE_INACTIVE
            WakeLockServiceState.ACTIVE -> Tile.STATE_ACTIVE
        },
        icon = when (wakeLockServiceState) {
            WakeLockServiceState.INACTIVE -> R.drawable.ic_inactive
            WakeLockServiceState.ACTIVE -> R.drawable.ic_active
        }
    )
}