package com.onthecrow.caffeine

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.onthecrow.caffeine.tile.CaffeineTileService
import com.onthecrow.caffeine.ui.theme.CaffeineTheme
import java.util.concurrent.Executor


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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = /*MaterialTheme.colorScheme.background*/Color.White
                ) {

                }
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CaffeineTheme {
    }
}