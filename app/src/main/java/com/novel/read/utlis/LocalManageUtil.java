package com.novel.read.utlis;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;

import com.novel.read.R;
import com.novel.read.widget.page.ReadSettingManager;

import java.util.Locale;

public class LocalManageUtil {

    private static final String TAG = "LocalManageUtil";

    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    public static Locale getSystemLocale(Context context) {
        return SpUtil.getInstance().getSystemCurrentLocal();
    }

    public static String getSelectLanguage(Context context) {
        switch (SpUtil.getInstance().getSelectLanguage()) {
            case 0:
                return context.getString(R.string.a_simple);
            case 1:
                return context.getString(R.string.a_traditional);
            default:
                return context.getString(R.string.a_simple);
        }
    }

    /**
     * 获取选择的语言设置
     *
     * @param context
     * @return
     */
    public static Locale getSetLanguageLocale(Context context) {

        switch (SpUtil.getInstance().getSelectLanguage()) {

            case 0:
                return Locale.CHINA;
            case 1:
                return Locale.TAIWAN;
            case 2:
            default:
                return Locale.ENGLISH;
        }
    }

    public static void saveSelectLanguage(Context context, int select) {
        SpUtil.getInstance().saveLanguage(select); //本地国际化
        ReadSettingManager.getInstance().setConvertType(select); //网络数据国际化
        setApplicationLanguage(context);
    }

    public static Context setLocal(Context context) {
        return updateResources(context, getSetLanguageLocale(context));
    }

    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    /**
     * 设置语言类型
     */
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    public static void saveSystemCurrentLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        Log.d(TAG, locale.getLanguage());
        SpUtil.getInstance().setSystemCurrentLocal(locale);
    }

    public static void onConfigurationChanged(Context context){
        saveSystemCurrentLanguage(context);
        setLocal(context);
        setApplicationLanguage(context);
    }
}
