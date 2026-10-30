package com.onthecrow.caffeine

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import java.lang.ref.WeakReference
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

enum class RunWithPermissionResult {
    SUCCESS,
    SHOW_RATIONALE,
    DENIED,
}

@Singleton
class PermissionRequester @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @SuppressLint("InlinedApi")
    private val permission: String = Manifest.permission.POST_NOTIFICATIONS
    private var registryOwners = ArrayDeque<WeakReference<ActivityResultRegistryOwner>>()
    private var permissionRequestLauncher: ActivityResultLauncher<String>? = null

    val isGranted: Boolean
        get() = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    init {
        (context as CaffeineApplication).registerActivityResultRegistryOwnerCallback(
            onRegistryOwnerChanged = { registryOwners.addFirst(WeakReference(it)) },
            onRegistryOwnerDestroyed = { owner -> registryOwners.removeIf { it.get() == owner } }
        )
    }

    suspend fun runWithPermission(runnable: Runnable): RunWithPermissionResult {
        return when {
            isGranted -> {
                runnable.run()
                RunWithPermissionResult.SUCCESS
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                takeFirst() as Activity,
                permission
            ) -> {
                RunWithPermissionResult.SHOW_RATIONALE
            }

            requestPermission() -> {
                runnable.run()
                RunWithPermissionResult.SUCCESS
            }

            else -> {
                RunWithPermissionResult.DENIED
            }
        }
    }

    /**
     * Fire-and-forget request that shows the system permission dialog and reports the
     * result via [onResult]. Safe to call without an [Activity] reference — it reuses the
     * currently registered [ActivityResultRegistryOwner]. No-ops if no owner is available.
     */
    fun request(onResult: (Boolean) -> Unit = {}) {
        if (isGranted) {
            onResult(true)
            return
        }
        val owner = takeFirstOrNull() ?: return
        permissionRequestLauncher?.unregister()
        permissionRequestLauncher = owner.activityResultRegistry.register(
            UUID.randomUUID().toString(),
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            onResult(granted)
            permissionRequestLauncher?.unregister()
            permissionRequestLauncher = null
        }
        permissionRequestLauncher?.launch(permission)
    }

    private suspend fun requestPermission(): Boolean {
        return callbackFlow {
            permissionRequestLauncher?.unregister()
            permissionRequestLauncher = takeFirst().activityResultRegistry.register(
                UUID.randomUUID().toString(),
                ActivityResultContracts.RequestPermission()
            ) {
                trySend(it)
            }
            permissionRequestLauncher?.launch(permission)
            awaitClose {
                permissionRequestLauncher?.unregister()
                permissionRequestLauncher = null
            }
        }.first()
    }

    private fun takeFirst(): ActivityResultRegistryOwner {
        return takeFirstOrNull() ?: throw IllegalStateException(
            "No ActivityResultRegistryOwner available, you have to " +
                    "instantiate this class in initialization block of an application class!"
        )
    }

    private fun takeFirstOrNull(): ActivityResultRegistryOwner? {
        while (registryOwners.isNotEmpty()) {
            val registryOwner = registryOwners.first().get()
            if (registryOwner != null) {
                return registryOwner
            } else {
                registryOwners.removeFirst()
            }
        }
        return null
    }
}