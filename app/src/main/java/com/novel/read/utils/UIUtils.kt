package com.novel.read.utils

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.novel.read.R
import com.novel.read.constant.Theme
import com.novel.read.utils.ext.*

@Suppress("unused")
object UIUtils {

    /** 设置更多工具条图标和颜色  */
    fun setToolbarMoreIconCustomColor(toolbar: Toolbar?, color: Int? = null) {
        toolbar ?: return
        val moreIcon = ContextCompat.getDrawable(toolbar.context, R.drawable.ic_launcher_background)
        if (moreIcon != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (color != null) {
                moreIcon.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
            toolbar.overflowIcon = moreIcon
        }
    }


    fun getMenuColor(
        context: Context,
        theme: Theme = Theme.Auto,
        requiresOverflow: Boolean = false
    ): Int {
        val defaultTextColor = context.getCompatColor(R.color.primaryText)
        if (requiresOverflow)
            return defaultTextColor
        val primaryTextColor = context.primaryTextColor
        return when (theme) {
            Theme.Dark -> context.getCompatColor(R.color.md_white_1000)
            Theme.Light -> context.getCompatColor(R.color.md_black_1000)
            else -> primaryTextColor
        }
    }

}