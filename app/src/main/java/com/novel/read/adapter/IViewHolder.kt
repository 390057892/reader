package com.novel.read.adapter

import android.view.View
import android.view.ViewGroup

/**
 * Created by zlj
 */

interface IViewHolder<T> {
    fun createItemView(parent: ViewGroup): View
    fun initView()
    fun onBind(data: T, pos: Int)
    fun onClick()
}
