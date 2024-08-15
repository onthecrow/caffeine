package com.onthecrow.caffeine.ui.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.onthecrow.caffeine.R
import com.onthecrow.caffeine.RequestResult
import com.onthecrow.caffeine.tile.CaffeineTileService
import com.onthecrow.caffeine.ui.theme.CaffeineTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
//                ContextCompat.startForegroundService(this, Intent(this, WakeLockService::class.java))
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CaffeineTheme {
                val isActive = remember { mutableStateOf(false) }
                MainContent(isActive)
            }
        }
        when {
            ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, POST_NOTIFICATIONS
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
//                showInContextUI(...)
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
        addTile()
        checkOverlayPermission()
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            // send user to the device settings
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
    }

    @RequiresApi(TIRAMISU)
    private fun addTile() {
        val resultSuccessExecutor = Executor {
//            runOnUiThread {
//                binding.txtResult.text = "requestAddTileService result success"
//            }
        }
        getSystemService(StatusBarManager::class.java).requestAddTileService(
            ComponentName(
                this,
                CaffeineTileService::class.java
            ),
            getString(R.string.app_name),
            Icon.createWithResource(this, R.drawable.ic_inactive),
            resultSuccessExecutor,
        ) { resultCodeFailure ->
            val resultFailureText =
                when (val ret = RequestResult.findByCode(resultCodeFailure)) {
                    RequestResult.TILE_ADD_REQUEST_ERROR_APP_NOT_IN_FOREGROUND,
                    RequestResult.TILE_ADD_REQUEST_ERROR_BAD_COMPONENT,
                    RequestResult.TILE_ADD_REQUEST_ERROR_MISMATCHED_PACKAGE,
                    RequestResult.TILE_ADD_REQUEST_ERROR_NOT_CURRENT_USER,
                    RequestResult.TILE_ADD_REQUEST_ERROR_NO_STATUS_BAR_SERVICE,
                    RequestResult.TILE_ADD_REQUEST_ERROR_REQUEST_IN_PROGRESS,
                    RequestResult.TILE_ADD_REQUEST_RESULT_TILE_ADDED,
                    RequestResult.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED,
                    RequestResult.TILE_ADD_REQUEST_RESULT_TILE_NOT_ADDED -> {
                        ret.name
                    }

                    null -> {
                        "unknown resultCodeFailure: $resultCodeFailure"
                    }
                }
//            runOnUiThread {
//                binding.txtResult.text = resultFailureText
//            }
        }
    }
}