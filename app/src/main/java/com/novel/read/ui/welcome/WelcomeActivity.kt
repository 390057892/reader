package com.novel.read.ui.welcome

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.hankcs.hanlp.HanLP
import com.permissionx.guolindev.PermissionX
import com.novel.read.R
import com.novel.read.base.BaseActivity
import com.novel.read.databinding.ActivityWelcomeBinding
import com.novel.read.help.AppConfig
import com.novel.read.ui.MainActivity
import com.novel.read.ui.read.ReadBookActivity
import com.novel.read.utils.ext.getPrefBoolean
import com.novel.read.help.coroutine.Coroutine
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {

    override fun getViewBinding(): ActivityWelcomeBinding {
        return ActivityWelcomeBinding.inflate(layoutInflater)
    }
    private var flag = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
        } else {
            init()
        }
    }

    private fun init() {
        Coroutine.async {
            //初始化简繁转换引擎
            when (AppConfig.chineseConverterType) {
                1 -> HanLP.convertToSimplifiedChinese("初始化")
                2 -> HanLP.convertToTraditionalChinese("初始化")
                else -> null
            }
        }

        PermissionX.init(this)
            .permissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    binding.rootView.postDelayed({ startMainActivity() }, 2500)
                } else {
                    toast("权限被拒绝,将导致部分功能异常,请到设置中开启相关权限")
//                    finish()
                }
            }

        binding.tvSkip.setOnClickListener { startMainActivity() }
    }

    @Synchronized
    private fun startMainActivity() {
        if (!flag) {
            startActivity<MainActivity>()
            if (getPrefBoolean(R.string.pk_default_read)) {
                startActivity<ReadBookActivity>()
            }
            finish()
        }
    }

    override fun onDestroy() {
        flag = true
        super.onDestroy()
    }

}