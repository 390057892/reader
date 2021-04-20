package com.novel.read.lib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.Window
import android.view.WindowInsetsController
import android.widget.EdgeEffect
import android.widget.ScrollView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.novel.read.App
import com.novel.read.R
import com.novel.read.help.AppConfig
import com.novel.read.help.TintHelper
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.ext.*
import org.jetbrains.anko.backgroundColor


/**
 * @author Karim Abou Zeid (kabouzeid)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object ATH {

    @SuppressLint("CommitPrefEdits")
    fun didThemeValuesChange(context: Context, since: Long): Boolean {
        return ThemeStore.isConfigured(context) && ThemeStore.prefs(context).getLong(
            ThemeStorePrefKeys.VALUES_CHANGED,
            -1
        ) > since
    }

    fun setStatusBarColorAuto(activity: Activity, fullScreen: Boolean) {
        val isTransparentStatusBar = AppConfig.isTransparentStatusBar
        setStatusBarColor(
            activity,
            ThemeStore.statusBarColor(activity, isTransparentStatusBar),
            isTransparentStatusBar, fullScreen
        )
    }

    fun setStatusBarColor(
        activity: Activity,
        color: Int,
        isTransparentStatusBar: Boolean,
        fullScreen: Boolean
    ) {
        if (fullScreen) {
            if (isTransparentStatusBar) {
                activity.window.statusBarColor = Color.TRANSPARENT
            } else {
                activity.window.statusBarColor = activity.getCompatColor(R.color.status_bar_bag)
            }
        } else {
            activity.window.statusBarColor = color
        }
        setLightStatusBarAuto(activity.window, color)
    }

    fun setLightStatusBarAuto(window: Window, bgColor: Int) {
        setLightStatusBar(window, ColorUtils.isColorLight(bgColor))
    }

    fun setLightStatusBar(window: Window, enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            val systemUiVisibility = decorView.systemUiVisibility
            if (enabled) {
                decorView.systemUiVisibility =
                    systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility =
                    systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }

    fun setLightStatusBarAuto(activity: Activity, bgColor: Int) {
        setLightStatusBar(activity, ColorUtils.isColorLight(bgColor))
    }

    fun setLightStatusBar(activity: Activity, enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.let {
                if (enabled) {
                    it.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                } else {
                    it.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
            }
        }
        setLightStatusBarO(activity, enabled)
    }

    @Suppress("DEPRECATION")
    private fun setLightStatusBarO(activity: Activity, enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = activity.window.decorView
            val systemUiVisibility = decorView.systemUiVisibility
            if (enabled) {
                decorView.systemUiVisibility =
                    systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility =
                    systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }


    fun setLightNavigationBar(activity: Activity, enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = activity.window.decorView
            var systemUiVisibility = decorView.systemUiVisibility
            systemUiVisibility = if (enabled) {
                systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
            decorView.systemUiVisibility = systemUiVisibility
        }
    }

    fun setNavigationBarColorAuto(
        activity: Activity,
        color: Int = ThemeStore.navigationBarColor(activity)
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.navigationBarColor = color
            setLightNavigationBar(activity, ColorUtils.isColorLight(color))
        }
    }

    fun setTaskDescriptionColorAuto(activity: Activity) {
        setTaskDescriptionColor(activity, ThemeStore.primaryColor(activity))
    }

    fun setTaskDescriptionColor(activity: Activity, @ColorInt color: Int) {
        val color1: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            color1 = ColorUtils.stripAlpha(color)
            @Suppress("DEPRECATION")
            activity.setTaskDescription(
                ActivityManager.TaskDescription(
                    activity.title as String,
                    null,
                    color1
                )
            )
        }
    }

    fun setTint(
        view: View,
        @ColorInt color: Int,
        isDark: Boolean = AppConfig.isNightTheme(view.context)
    ) {
        TintHelper.setTintAuto(view, color, false, isDark)
    }

    fun setBackgroundTint(
        view: View, @ColorInt color: Int,
        isDark: Boolean = AppConfig.isNightTheme
    ) {
        TintHelper.setTintAuto(view, color, true, isDark)
    }

    fun setAlertDialogTint(dialog: AlertDialog): AlertDialog {
        dialog.window?.setBackgroundDrawable(getDialogBackground())
        val colorStateList = Selector.colorBuild()
            .setDefaultColor(ThemeStore.accentColor(dialog.context))
            .setPressedColor(ColorUtils.darkenColor(ThemeStore.accentColor(dialog.context)))
            .create()
        if (dialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorStateList)
        }
        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorStateList)
        }
        if (dialog.getButton(AlertDialog.BUTTON_NEUTRAL) != null) {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(colorStateList)
        }
        return dialog
    }

    fun setEdgeEffectColor(view: RecyclerView?, @ColorInt color: Int) {
        view?.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                val edgeEffect = super.createEdgeEffect(view, direction)
                edgeEffect.color = color
                return edgeEffect
            }
        }
    }

    fun setEdgeEffectColor(viewPager: ViewPager?, @ColorInt color: Int) {
        try {
            val clazz = ViewPager::class.java
            for (name in arrayOf("mLeftEdge", "mRightEdge")) {
                val field = clazz.getDeclaredField(name)
                field.isAccessible = true
                val edge = field.get(viewPager)
                (edge as EdgeEffect).color = color
            }
        } catch (ignored: Exception) {
        }
    }

    fun setEdgeEffectColor(scrollView: ScrollView?, @ColorInt color: Int) {
        try {
            val clazz = ScrollView::class.java
            for (name in arrayOf("mEdgeGlowTop", "mEdgeGlowBottom")) {
                val field = clazz.getDeclaredField(name)
                field.isAccessible = true
                val edge = field.get(scrollView)
                (edge as EdgeEffect).color = color
            }
        } catch (ignored: Exception) {
        }
    }

    //**************************************************************Directly*************************************************************//

    fun applyBottomNavigationColor(bottomBar: BottomNavigationView) {
        bottomBar.apply {
            val bgColor = context.bottomBackground
            setBackgroundColor(bgColor)
            val textIsDark = ColorUtils.isColorLight(bgColor)
            val textColor = context.getSecondaryTextColor(textIsDark)
            val colorStateList = Selector.colorBuild()
                .setDefaultColor(textColor)
                .setSelectedColor(ThemeStore.accentColor(context)).create()
            itemIconTintList = colorStateList
            itemTextColor = colorStateList
        }
    }

    fun applyAccentTint(view: View?) {
        view?.apply {
            setTint(this, context.accentColor)
        }
    }

    fun applyBackgroundTint(view: View?) {
        view?.apply {
            if (background == null) {
                backgroundColor = context.backgroundColor
            } else {
                setBackgroundTint(this, context.backgroundColor)
            }
        }
    }

    fun applyEdgeEffectColor(view: View?) {
        when (view) {
            is RecyclerView -> view.edgeEffectFactory = DEFAULT_EFFECT_FACTORY
            is ViewPager -> setEdgeEffectColor(view, ThemeStore.primaryColor(view.context))
            is ScrollView -> setEdgeEffectColor(view, ThemeStore.primaryColor(view.context))
        }
    }

    fun getDialogBackground(): GradientDrawable {
        val background = GradientDrawable()
        background.cornerRadius = 3F.dp
        background.setColor(App.INSTANCE.backgroundColor)
        return background
    }

    private val DEFAULT_EFFECT_FACTORY = object : RecyclerView.EdgeEffectFactory() {
        override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
            val edgeEffect = super.createEdgeEffect(view, direction)
            edgeEffect.color = ThemeStore.primaryColor(view.context)
            return edgeEffect
        }
    }
}