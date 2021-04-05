package com.novel.read.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.novel.read.R
import com.novel.read.lib.Selector
import com.novel.read.lib.ThemeStore
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.ext.*

class AccentBgTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var radius = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AccentBgTextView)
        radius = typedArray.getDimensionPixelOffset(R.styleable.AccentBgTextView_radius, radius)
        typedArray.recycle()
        upBackground()
        setTextColor(Color.WHITE)
    }

    fun setRadius(radius: Int) {
        this.radius = radius.dp
        upBackground()
    }

    private fun upBackground() {
        background = if (isInEditMode) {
            Selector.shapeBuild()
                .setCornerRadius(radius)
                .setDefaultBgColor(context.getCompatColor(R.color.accent))
                .setPressedBgColor(ColorUtils.darkenColor(context.getCompatColor(R.color.accent)))
                .create()
        } else {
            Selector.shapeBuild()
                .setCornerRadius(radius)
                .setDefaultBgColor(ThemeStore.accentColor(context))
                .setPressedBgColor(ColorUtils.darkenColor(ThemeStore.accentColor(context)))
                .create()
        }
    }
}
