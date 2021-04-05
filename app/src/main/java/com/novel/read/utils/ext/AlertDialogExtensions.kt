package com.novel.read.utils.ext

import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.novel.read.lib.ATH

fun AlertDialog.applyTint(): AlertDialog {
    return ATH.setAlertDialogTint(this)
}

fun AlertDialog.requestInputMethod(){
    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
}
