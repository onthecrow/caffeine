package com.onthecrow.caffeine.service.tile

import android.service.quicksettings.Tile
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.onthecrow.caffeine.R
import com.onthecrow.caffeine.service.caffeine.CaffeineServiceState

data class CaffeineTileState(
    val state: Int = Tile.STATE_INACTIVE,
    @StringRes val label: Int = R.string.app_name,
    @StringRes val contentDescription: Int = R.string.tile_content_description,
    @DrawableRes val icon: Int = R.drawable.ic_inactive,
)

fun mapWakeLockStateToTileState(caffeineServiceState: CaffeineServiceState): CaffeineTileState {
    return CaffeineTileState(
        state = when (caffeineServiceState) {
            CaffeineServiceState.INACTIVE -> Tile.STATE_INACTIVE
            CaffeineServiceState.ACTIVE -> Tile.STATE_ACTIVE
        },
        icon = when (caffeineServiceState) {
            CaffeineServiceState.INACTIVE -> R.drawable.ic_inactive
            CaffeineServiceState.ACTIVE -> R.drawable.ic_active
        }
    )
}