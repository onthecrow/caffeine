package com.onthecrow.caffeine.wakelock

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


class WakeLockViewWrapper(private val context: Context) {

    private val view by lazy { View(context).apply { keepScreenOn = true } }
    private val windowManager by lazy { context.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    fun isShown(): Boolean {
        return view.parent != null
    }

    fun show() {
        try {
            if (view.windowToken == null && view.parent == null) {
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
        } catch (error: Throwable) {
            Log.d(javaClass.simpleName, error.toString())
        }
    }

    fun remove() {
        try {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(view)
            view.invalidate()
            (view.parent as ViewGroup).removeAllViews()
        } catch (error: Throwable) {
            Log.d(javaClass.simpleName, error.toString())
        }
    }
}