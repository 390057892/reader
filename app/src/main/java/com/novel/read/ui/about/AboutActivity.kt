package com.novel.read.ui.about

import android.os.Bundle
import com.novel.read.R
import com.novel.read.base.BaseActivity
import com.novel.read.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
    }

    override fun getViewBinding(): ActivityAboutBinding {
        return ActivityAboutBinding.inflate(layoutInflater)
    }

}