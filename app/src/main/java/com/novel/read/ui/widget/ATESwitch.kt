package com.novel.read.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.accentColor

/**
 * @author Aidan Follestad (afollestad)
 */
class ATESwitch(context: Context, attrs: AttributeSet) : SwitchCompat(context, attrs) {

    init {
        if (!isInEditMode) {
            ATH.setTint(this, context.accentColor)
        }

    }

}
