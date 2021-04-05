package com.novel.read.utils.ext

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowMetrics


inline fun <reified A : Activity> Activity.startActivityForResult(
    requestCode: Int,
    configIntent: Intent.() -> Unit = {}
) {
    startActivityForResult(Intent(this, A::class.java).apply(configIntent), requestCode)
}


fun Activity.getSize(): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        displayMetrics.widthPixels = windowMetrics.bounds.width() - insets.left - insets.right
        displayMetrics.heightPixels = windowMetrics.bounds.height() - insets.top - insets.bottom
    } else {
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
    }
    return displayMetrics
}
