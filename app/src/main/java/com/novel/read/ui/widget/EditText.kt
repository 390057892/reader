package com.novel.read.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.novel.read.lib.ATH
import com.novel.read.lib.ThemeStore


class EditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    init {
        if (!isInEditMode) {
            ATH.setTint(this, ThemeStore.accentColor(context))
        }
    }
}
