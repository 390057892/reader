package com.novel.read.utlis

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

/**
 * create by zlj on 2019/6/19
 * describe:
 */
class DialogUtils private constructor() {

    fun showAlertDialog(
        context: Context,
        msg: String,
        dialogListener: DialogInterface.OnClickListener
    ) {
        // context = context.getApplicationContext();
        val builder = AlertDialog.Builder(context)
        builder.setTitle("操作提示").setMessage(msg).setCancelable(false)
            .setPositiveButton("确定", dialogListener)
            .setNegativeButton("取消") { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    companion object {

        private var instance: DialogUtils? = null

        @Synchronized
        fun getInstance(): DialogUtils {
            if (instance == null) {
                instance = DialogUtils()
            }
            return instance as DialogUtils
        }
    }
}
