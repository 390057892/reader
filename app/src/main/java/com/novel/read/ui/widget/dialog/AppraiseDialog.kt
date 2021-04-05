package com.novel.read.ui.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.novel.read.R
import kotlinx.android.synthetic.main.dialog_go_appraise.*

class AppraiseDialog(context: Context) : Dialog(context, R.style.dialog) {
    private val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_go_appraise, null)

    init {
        view.setHasTransientState(true)
        setContentView(
            view, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )
        setCancelable(false)
        initView()

    }

    private fun initView() {
        val mTvCancel = findViewById<TextView>(R.id.tv_cancel)

        mTvCancel.setOnClickListener { dismiss() }
    }

    fun appraiseDialog(receiveClickListener: View.OnClickListener) {
        tv_appraise.setOnClickListener(receiveClickListener)
    }

}