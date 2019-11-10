package com.novel.read.utlis

import android.os.Build
import android.view.View
import android.view.WindowManager

import androidx.appcompat.app.AppCompatActivity

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * @author: LiJun 390057892@qq.com
 * @date: 2018/3/7 15:07
 */

object StatusBarUtil {
    fun setBarsStyle(activity: AppCompatActivity, color: Int, dark: Boolean) {
        MIUISetStatusBarLightMode(activity, dark)
        FlymeSetStatusBarLightMode(activity, dark)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            val decorView = activity.window.decorView
            if (dark) {
                val option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                decorView.systemUiVisibility = option
                activity.window.statusBarColor = activity.resources.getColor(color)
            } else {
                val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                decorView.systemUiVisibility = option
                activity.window.statusBarColor = activity.resources.getColor(color)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            val localLayoutParams = activity.window.attributes
            localLayoutParams.flags =
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags
        }

        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
        //            if (dark){
        //                activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //            }else {
        //                activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        //            }
        //
        //        }
    }

    fun MIUISetStatusBarLightMode(activity: AppCompatActivity, dark: Boolean): Boolean {
        var result = false
        if (activity.window != null) {
            val clazz = activity.window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod(
                    "setExtraFlags",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                if (dark) {
                    extraFlagField.invoke(activity.window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(activity.window, 0, darkModeFlag)//清除黑色字体
                }
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }

    private fun FlymeSetStatusBarLightMode(activity: AppCompatActivity, dark: Boolean): Boolean {
        var result = false
        if (activity.window != null) {
            try {
                val lp = activity.window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                    .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (dark) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                activity.window.attributes = lp
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }


}
