package com.novel.read.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.view.ViewGroup


@Suppress("unused")
object SystemUtils {

    private const val NAVIGATION = "navigationBarBackground"

    fun getScreenOffTime(context: Context): Int {
        var screenOffTime = 0
        try {
            screenOffTime = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return screenOffTime
    }

    fun ignoreBatteryOptimization(activity: Activity) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) return

        val powerManager = activity.getSystemService(POWER_SERVICE) as PowerManager
        val hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.packageName)
        //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        if (!hasIgnored) {
            try {
                @SuppressLint("BatteryLife")
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivity(intent)
            } catch (ignored: Throwable) {
            }

        }
    }

    /**
     * 返回NavigationBar是否存在
     * 该方法需要在View完全被绘制出来之后调用，否则判断不了
     * 在比如 onWindowFocusChanged（）方法中可以得到正确的结果
     */
    fun isNavigationBarExist(activity: Activity?): Boolean {
        activity?.let {
            val vp = it.window.decorView as? ViewGroup
            if (vp != null) {
                for (i in 0 until vp.childCount) {
                    vp.getChildAt(i).context.packageName
                    if (vp.getChildAt(i).id != View.NO_ID
                        && NAVIGATION == activity.resources.getResourceEntryName(vp.getChildAt(i).id)
                    ) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
