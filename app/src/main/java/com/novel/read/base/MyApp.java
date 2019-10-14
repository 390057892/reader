package com.novel.read.base;

import android.app.Application;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;


import androidx.appcompat.app.AppCompatDelegate;

import com.novel.read.constants.Constant;
import com.novel.read.service.DownloadService;
import com.novel.read.utlis.LocalManageUtil;
import com.novel.read.utlis.SpUtil;
import com.tencent.bugly.crashreport.CrashReport;


import org.litepal.LitePal;

/**
 * create by 赵利君 on 2019/6/10
 * describe:
 */
public class MyApp extends Application {

    private static Context sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        LitePal.initialize(this);
        setNight();
        LocalManageUtil.setApplicationLanguage(this);
        startService(new Intent(getContext(), DownloadService.class));
        CrashReport.initCrashReport(getApplicationContext(), Constant.buglyId, false);

    }

    private void setNight() {
        if (SpUtil.getBooleanValue(Constant.NIGHT, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static Context getContext() {
        return sInstance;
    }


    @Override
    protected void attachBaseContext(Context base) {
        SpUtil.init(base);
        //保存系统选择语言
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(LocalManageUtil.setLocal(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //保存系统选择语言
        LocalManageUtil.onConfigurationChanged(getApplicationContext());
    }


}
