package com.novel.read.ui.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.*
import android.widget.LinearLayout
import com.novel.read.R

class LoginDialog(context: Context) : Dialog(context, R.style.dialog) {
    private val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_login, null)

    init {
        view.setHasTransientState(true)
        setContentView(
            view, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )
        setCancelable(true)
        initView()

    }

    private fun initView() {

    }

    fun LoginDialog(receiveClickListener: View.OnClickListener) {
    }

}