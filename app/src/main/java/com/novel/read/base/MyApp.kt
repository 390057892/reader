package com.novel.read.base

import android.app.Application

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log


import androidx.appcompat.app.AppCompatDelegate

import com.novel.read.constants.Constant
import com.novel.read.service.DownloadService
import com.novel.read.utlis.LocalManageUtil
import com.novel.read.utlis.SpUtil


import org.litepal.LitePal
import kotlin.properties.Delegates

/**
 * create by zlj on 2019/6/10
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        LitePal.initialize(this)
        setNight()
        LocalManageUtil.setApplicationLanguage(this)
        startService(Intent(context, DownloadService::class.java))
    }

    private fun setNight() {
        if (SpUtil.getBooleanValue(Constant.NIGHT, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    override fun attachBaseContext(base: Context) {
        SpUtil.init(base)
        //保存系统选择语言
        LocalManageUtil.saveSystemCurrentLanguage(base)
        super.attachBaseContext(LocalManageUtil.setLocal(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //保存系统选择语言
        LocalManageUtil.onConfigurationChanged(applicationContext)
    }

    companion object {
        var context: Context by Delegates.notNull()
            private set
    }


}
