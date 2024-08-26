package com.onthecrow.caffeine.service.tile

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.onthecrow.caffeine.service.caffeine.CaffeineServiceConnectionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@AndroidEntryPoint
class CaffeineTileService : TileService() {

    @Inject
    lateinit var caffeineServiceConnectionManager: CaffeineServiceConnectionManager

    private var coroutineScope: CoroutineScope? = null
    private var tileState: StateFlow<CaffeineTileState>? = null

    override fun onStartListening() {
        super.onStartListening()
        coroutineScope = CoroutineScope(Dispatchers.Main + Job())
        tileState = caffeineServiceConnectionManager.serviceState
            .map(::mapWakeLockStateToTileState)
            .onEach(::updateTileState)
            .stateIn(coroutineScope!!, SharingStarted.Eagerly, CaffeineTileState())
    }

    override fun onClick() {
        super.onClick()
        when (tileState?.value?.state) {
            Tile.STATE_ACTIVE -> caffeineServiceConnectionManager.stop()
            else -> caffeineServiceConnectionManager.start()
        }
    }

    override fun onStopListening() {
        super.onStopListening()
        coroutineScope?.cancel()
        coroutineScope = null
        tileState = null
    }

    private fun updateTileState(caffeineTileState: CaffeineTileState) {
        with(qsTile) {
            label = getString(caffeineTileState.label)
            contentDescription = getString(caffeineTileState.contentDescription)
            state = caffeineTileState.state
            icon = Icon.createWithResource(applicationContext, caffeineTileState.icon)
            updateTile()
        }
        Log.d(
            this.javaClass.simpleName, """
            Tile updated ($caffeineTileState)
        """.trimIndent()
        )
    }
}