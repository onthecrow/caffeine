package com.onthecrow.caffeine.wakelock

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.onthecrow.caffeine.core.logger.FileLogger.log
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class WakeLockViewWrapper(private val context: Context) {

    /**
     * Workaround to make it more persistent because of Android clears system overlay every 24 hours approx.
     */
    var isPersistent by Delegates.observable(false) { _, _, _ -> toggleRenewalIfPersistent() }

    private val screenStateReceiver by lazy(LazyThreadSafetyMode.NONE) { ScreenStateReceiver() }
    private var viewShownCheckerJob: Job? = null

    // todo Allow screen overlays on settings screen (developer settings)
    private val view by lazy {
        View(context).apply {
            keepScreenOn = true
        }
    }

    private val windowManager by lazy { context.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    fun isShown(): Boolean {
        // todo maybe should replace || with &&
        return view.parent != null || view.windowToken != null
    }

    fun show() {
        try {
            if (!isShown()) {
                addView()
                screenStateReceiver.register(context) { screenState: ScreenState ->
                    toggleRenewalIfPersistent(screenState == ScreenState.SCREEN_ON)
                }
            }
        } catch (error: Throwable) {
            Log.d(javaClass.simpleName, error.toString())
        }
    }

    fun remove() {
        try {
            cancelPersistentJob()
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(view)
//            view.invalidate()
//            (view.parent as ViewGroup).removeAllViews()
            screenStateReceiver.unregister(context)
        } catch (error: Throwable) {
            log(error.toString())
        }
    }

    private fun addView() {
        windowManager.addView(
            view,
            WindowManager.LayoutParams(
                0,
                0,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            )
        )
    }

    private fun toggleRenewalIfPersistent(shouldRenew: Boolean? = null) {
        log("toggle persistent job (shouldRenew = $shouldRenew)")
        if (!isPersistent) {
            cancelPersistentJob()
            return
        }
        if (shouldRenew ?: (screenStateReceiver.currentScreenState == ScreenState.SCREEN_ON)) {
            viewShownCheckerJob?.cancel()
            viewShownCheckerJob = MainScope().launch {
                while (this.isActive) {
                    log("persistent job iteration")
                    delay(10000)
                    if (!isShown()) {
                        addView()
                        log("view is added again")
                    }
                }
            }
        } else {
            cancelPersistentJob()
        }
    }

    private fun cancelPersistentJob() {
        log("persistent job is cancelled")
        viewShownCheckerJob?.cancel()
        viewShownCheckerJob = null
    }
}