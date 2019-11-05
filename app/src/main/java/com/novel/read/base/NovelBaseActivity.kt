package com.novel.read.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.novel.read.R
import com.novel.read.constants.Constant
import com.novel.read.utlis.LocalManageUtil
import com.novel.read.utlis.SpUtil
import com.novel.read.utlis.StatusBarUtil

/**
 * create by 赵利君 on 2019/6/10
 * describe:
 */
abstract class NovelBaseActivity : AppCompatActivity() {

    private var mNowMode: Boolean = false

    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setBarsStyle(this, R.color.colorPrimary, true)
        mNowMode = SpUtil.getBooleanValue(Constant.NIGHT)
        setContentView(layoutId)
        initView()
        initData()

    }

    private fun setTheme() {
        if (SpUtil.getBooleanValue(Constant.NIGHT) != mNowMode) {
            if (SpUtil.getBooleanValue(Constant.NIGHT)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            recreate()
        }
    }

    protected abstract fun initView()

    protected abstract fun initData()

    override fun onResume() {
        super.onResume()
        setTheme()
    }

    protected fun gone(vararg views: View) {
        if (views.isNotEmpty()) {
            for (view in views) {
                view.visibility = View.GONE
            }
        }
    }

    protected fun visible(vararg views: View) {
        if (views.isNotEmpty()) {
            for (view in views) {
                view.visibility = View.VISIBLE
            }
        }
    }

    protected fun isVisible(view: View): Boolean {
        return view.visibility == View.VISIBLE
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase))
    }
}
