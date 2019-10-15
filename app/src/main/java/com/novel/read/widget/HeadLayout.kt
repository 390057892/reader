package com.novel.read.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.novel.read.R
import kotlinx.android.synthetic.main.widget_head.view.*

class HeadLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_head, this, true)
        initAttr(attrs)
    }

    private fun initAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeadLayout)
        val showHead = typedArray.getBoolean(R.styleable.HeadLayout_showHead, true)
        val headText = typedArray.getResourceId(R.styleable.HeadLayout_headText, R.string.empty_info)
        val headImg = typedArray.getResourceId(R.styleable.HeadLayout_headImg, R.mipmap.ic_launcher)
        val showRight = typedArray.getBoolean(R.styleable.HeadLayout_showRightText, true)
        val rightText = typedArray.getResourceId(R.styleable.HeadLayout_rightText, R.string.empty_info)
        val rightImg = typedArray.getResourceId(R.styleable.HeadLayout_rightImg, R.mipmap.ic_launcher)
        if (showHead) {
            tv_head.setText(headText)
            val drawable = ContextCompat.getDrawable(context, headImg)
            drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            tv_head.setCompoundDrawables(drawable, null, null, null)
        } else {
            tv_head.visibility = View.GONE
        }
        if (showRight) {
            tv_more.setText(rightText)
            if (rightImg != R.mipmap.ic_launcher) {
                val drawable = ContextCompat.getDrawable(context, rightImg)
                drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                tv_head.setCompoundDrawables(null, null, drawable, null)
            }
        } else {
            tv_more.visibility = View.GONE
        }

        typedArray.recycle()
    }

    //设置点击右边文字的监听
    fun setRightTextClickListener(listener: View.OnClickListener) {
        tv_more.setOnClickListener(listener)
    }


}
