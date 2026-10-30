package com.onthecrow.caffeine.core

import android.app.Activity
import android.app.Application
import android.os.Bundle

fun interface ActivityResumedCallback : Application.ActivityLifecycleCallbacks {

    fun onActivityPaused()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // Do nothing
    }

    override fun onActivityStarted(activity: Activity) {
        // Do nothing
    }

    override fun onActivityResumed(activity: Activity) {
        onActivityPaused()
    }

    override fun onActivityPaused(activity: Activity) {
        // Do nothing
    }

    override fun onActivityStopped(activity: Activity) {
        // Do nothing
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // Do nothing
    }

    override fun onActivityDestroyed(activity: Activity) {
        // Do nothing
    }
}