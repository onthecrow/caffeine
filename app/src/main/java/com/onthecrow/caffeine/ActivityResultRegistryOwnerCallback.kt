package com.onthecrow.caffeine

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.activity.result.ActivityResultRegistryOwner

fun Application.registerActivityResultRegistryOwnerCallback(
    onRegistryOwnerChanged: (ActivityResultRegistryOwner) -> Unit,
    onRegistryOwnerDestroyed: (ActivityResultRegistryOwner) -> Unit,
) {
    registerActivityLifecycleCallbacks(
        object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?
            ) {
                onRegistryOwnerChanged(activity as ActivityResultRegistryOwner)
            }

            override fun onActivityDestroyed(activity: Activity) {
                onRegistryOwnerDestroyed(activity as ActivityResultRegistryOwner)
            }

            override fun onActivityStarted(activity: Activity) { }

            override fun onActivityResumed(activity: Activity) { }

            override fun onActivityPaused(activity: Activity) { }

            override fun onActivityStopped(activity: Activity) { }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }
        }
    )
}
