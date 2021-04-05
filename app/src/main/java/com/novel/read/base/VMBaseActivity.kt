package com.novel.read.base

import androidx.lifecycle.ViewModel
import com.novel.read.constant.Theme

abstract class VMBaseActivity<VM : ViewModel>(
    layoutID: Int,
    fullScreen: Boolean = true,
    theme: Theme = Theme.Auto,
    toolBarTheme: Theme = Theme.Auto
) : BaseActivity(layoutID, fullScreen, theme, toolBarTheme) {

    protected abstract val viewModel: VM

}