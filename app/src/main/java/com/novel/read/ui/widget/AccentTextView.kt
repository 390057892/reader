package com.novel.read.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.novel.read.R
import com.novel.read.utils.ext.accentColor
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textColorResource

class AccentTextView(context: Context, attrs: AttributeSet?) :
    AppCompatTextView(context, attrs) {

    init {
        if (!isInEditMode) {
            textColor = context.accentColor
        } else {
            textColorResource = R.color.accent
        }
    }

}
