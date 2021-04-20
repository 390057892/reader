package com.novel.read.ui.setting

import android.os.Bundle
import com.novel.read.R
import com.novel.read.base.BaseActivity
import com.novel.read.constant.EventBus
import com.novel.read.databinding.ActivitySettingActivityBinding
import com.novel.read.utils.ext.observeEvent

class SettingActivity : BaseActivity<ActivitySettingActivityBinding>() {

    override fun getViewBinding(): ActivitySettingActivityBinding {
        return ActivitySettingActivityBinding.inflate(layoutInflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val fTag = "otherConfigFragment"
        var configFragment = supportFragmentManager.findFragmentByTag(fTag)
        if (configFragment == null) configFragment = OtherConfigFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.configFrameLayout, configFragment, fTag)
            .commit()
    }

    override fun observeLiveBus() {
        super.observeLiveBus()
        observeEvent<String>(EventBus.RECREATE) {
            recreate()
        }
    }


}