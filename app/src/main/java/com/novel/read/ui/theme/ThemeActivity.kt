package com.novel.read.ui.theme

import android.os.Bundle
import com.novel.read.R
import com.novel.read.base.BaseActivity
import com.novel.read.databinding.ActivityThemeBinding

class ThemeActivity : BaseActivity<ActivityThemeBinding>() {

    override fun getViewBinding(): ActivityThemeBinding {
        return ActivityThemeBinding.inflate(layoutInflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

    }

}