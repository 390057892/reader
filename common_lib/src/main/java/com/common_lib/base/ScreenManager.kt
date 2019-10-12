package com.common_lib.base

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.DisplayMetrics
import kotlin.math.abs

/**
 * create by 赵利君 on 2019/10/12
 * describe:
 */
class ScreenManager private constructor() {
    //this is the screen width dpi the max value is 1200

    private var screenWidth = -1
    private var screenHeight = -1
    private var dm: DisplayMetrics? = null
    private var density: Float = 0.toFloat()
    private var isScreenSetup = false

    val margin: Int
        get() = (30.0 / 750 * ScreenManager.instance!!.getScreenWidth()).toInt()

    fun setUpScreen(activity: Activity) {
        if (!isScreenSetup) {
            dm = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(dm)
            dm?.let {
                density = it.density
                screenWidth = abs(it.widthPixels)
                screenHeight = abs(it.heightPixels)
            }

            isScreenSetup = true
        }
    }

    private fun getScreenWidth(): Int {
        assert(isScreenSetup)
        return screenWidth
    }


    fun getScreenHeight(): Int {
        assert(isScreenSetup)
        return screenHeight
    }

    fun getDensity(): Float {
        assert(isScreenSetup)
        return density
    }

    fun getPxFromDp(dp: Int): Int {
        return (dp * density + 0.5).toInt()
    }

    fun getDpFromPx(px: Int): Int {
        return (px / density + 0.5).toInt()
    }

    /**
     * @param number      每行显示的控件个数
     * @param normalSplit 控件之间的间距
     * @param leftMargin  控件左侧和container的距离
     * @param rightMargin 控件右侧和container的距离
     * @return
     */
    fun getViewSize(number: Int, normalSplit: Int, leftMargin: Int, rightMargin: Int): Int {
        var sp = 0
        if (number > 1) {
            sp = number - 1
        }
        //        int totalSplit=Math.round(sp*normalSplit*density+(leftMargin+rightMargin)*density);
        val totalSplit =
            getScreenWidth() - (number - 1) * getPxFromDp(normalSplit) - getPxFromDp(rightMargin) - getPxFromDp(
                leftMargin
            )
        return totalSplit / number
    }

    fun getPxFromSp(sp: Int, context: Context): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (sp * fontScale + 0.5f).toInt()
    }

    companion object {

        @get:Synchronized
        var instance: ScreenManager? = null
            private set

        init {
            instance = ScreenManager()
        }

        /**
         * 屏幕截图
         * @param activity
         * @return
         */
        fun captureScreen(activity: Activity): Bitmap {
            activity.window.decorView.isDrawingCacheEnabled = true
            return activity.window.decorView.drawingCache
        }
    }

}
