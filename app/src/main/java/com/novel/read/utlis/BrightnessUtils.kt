package com.novel.read.utlis

import android.app.Activity
import android.content.ContentResolver
import android.provider.Settings
import android.util.Log
import android.view.WindowManager

/**
 * Created by zlj
 * 调节亮度的工具类
 */

object BrightnessUtils {
    private val TAG = "BrightnessUtils"

    /**
     * 判断是否开启了自动亮度调节
     */
    fun isAutoBrightness(activity: Activity): Boolean {
        var isAuto = false
        try {
            isAuto = Settings.System.getInt(
                activity.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            ) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }

        return isAuto
    }

    /**
     * 获取屏幕的亮度
     * 系统亮度模式中，自动模式与手动模式获取到的系统亮度的值不同
     */
    fun getScreenBrightness(activity: Activity): Int {
        return if (isAutoBrightness(activity)) {
            getAutoScreenBrightness(activity)
        } else {
            getManualScreenBrightness(activity)
        }
    }

    /**
     * 获取手动模式下的屏幕亮度
     * @return value:0~255
     */
    fun getManualScreenBrightness(activity: Activity): Int {
        var nowBrightnessValue = 0
        val resolver = activity.contentResolver
        try {
            nowBrightnessValue = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return nowBrightnessValue
    }

    /**
     * 获取自动模式下的屏幕亮度
     * @return value:0~255
     */
    fun getAutoScreenBrightness(activity: Activity): Int {
        var nowBrightnessValue = 0f

        //获取自动调节下的亮度范围在 0~1 之间
        val resolver = activity.contentResolver
        try {
            nowBrightnessValue =
                Settings.System.getFloat(resolver, Settings.System.SCREEN_BRIGHTNESS)
            Log.d(TAG, "getAutoScreenBrightness: $nowBrightnessValue")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //转换范围为 (0~255)
        val fValue = nowBrightnessValue * 225.0f
        Log.d(TAG, "brightness: $fValue")
        return fValue.toInt()
    }

    /**
     * 设置亮度:通过设置 Windows 的 screenBrightness 来修改当前 Windows 的亮度
     * lp.screenBrightness:参数范围为 0~1
     */
    fun setBrightness(activity: Activity, brightness: Int) {
        try {
            val lp = activity.window.attributes
            //将 0~255 范围内的数据，转换为 0~1
            lp.screenBrightness = java.lang.Float.valueOf(brightness.toFloat()) * (1f / 255f)
            Log.d(TAG, "lp.screenBrightness == " + lp.screenBrightness)
            activity.window.attributes = lp
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    /**
     * 获取当前系统的亮度
     * @param activity
     */
    fun setDefaultBrightness(activity: Activity) {
        try {
            val lp = activity.window.attributes
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            activity.window.attributes = lp
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
}
