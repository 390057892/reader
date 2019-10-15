package com.novel.read.base


import android.content.Intent
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment


abstract class NovelBaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(),null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId():Int

    abstract fun initView()

    abstract fun initData()

    fun toActivity(toClsActivity: Class<*>) {
        this.toActivity(toClsActivity, null as Bundle?)
    }

    fun toActivity(toClsActivity: Class<*>, bundle: Bundle?) {
        val intent = Intent(this.context, toClsActivity)
        if (bundle != null) {
            intent.putExtras(bundle)
        }

        this.startActivity(intent)
    }

    fun toActivityForResult(toClsActivity: Class<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent(this.context, toClsActivity)
        if (bundle != null) {
            intent.putExtras(bundle)
        }

        startActivityForResult(intent, requestCode)
    }


}
