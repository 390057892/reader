package com.novel.read.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import com.novel.read.R
import com.novel.read.lib.Selector
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.ext.*

/**
 * @author Aidan Follestad (afollestad)
 */
class ATERadioNoButton(context: Context, attrs: AttributeSet) :
    AppCompatRadioButton(context, attrs) {

    private val isBottomBackground: Boolean

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ATERadioNoButton)
        isBottomBackground =
            typedArray.getBoolean(R.styleable.ATERadioNoButton_isBottomBackground, false)
        typedArray.recycle()
        initTheme()
    }

    private fun initTheme() {
        when {
            isInEditMode -> Unit
            isBottomBackground -> {
                val isLight = ColorUtils.isColorLight(context.bottomBackground)
                val textColor = context.getPrimaryTextColor(isLight)
                background = Selector.shapeBuild()
                    .setCornerRadius(1.dp)
                    .setStrokeWidth(1.dp)
                    .setCheckedBgColor(context.accentColor)
                    .setCheckedStrokeColor(context.accentColor)
                    .setDefaultStrokeColor(textColor)
                    .create()
                setTextColor(
                    Selector.colorBuild()
                        .setDefaultColor(textColor)
                        .setCheckedColor(context.getPrimaryTextColor(ColorUtils.isColorLight(context.accentColor)))
                        .create()
                )
            }
            else -> {
                val textColor = context.getCompatColor(R.color.primaryText)
                background = Selector.shapeBuild()
                    .setCornerRadius(1.dp)
                    .setStrokeWidth(1.dp)
                    .setCheckedBgColor(context.accentColor)
                    .setCheckedStrokeColor(context.accentColor)
                    .setDefaultStrokeColor(textColor)
                    .create()
                setTextColor(
                    Selector.colorBuild()
                        .setDefaultColor(textColor)
                        .setCheckedColor(context.getPrimaryTextColor(ColorUtils.isColorLight(context.accentColor)))
                        .create()
                )
            }
        }

    }

}
