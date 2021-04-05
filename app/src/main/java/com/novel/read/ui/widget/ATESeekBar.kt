package com.novel.read.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.accentColor

/**
 * @author Aidan Follestad (afollestad)
 */
class ATESeekBar(context: Context, attrs: AttributeSet) : AppCompatSeekBar(context, attrs) {

    init {
        ATH.setTint(this, context.accentColor)
    }
}
