package com.novel.read.utlis;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Locale;

public class SpUtil {

    static SharedPreferences sp;
    private static SpUtil sInstance;

    public static void init(Context context) {
        sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static SpUtil getInstance(){
        if(sInstance == null){
            synchronized (SpUtil.class){
                if (sInstance == null){
                    sInstance = new SpUtil();
                }
            }
        }
        return sInstance;
    }

    public static String getStringValue(String key) {
        return sp.getString(key, null);
    }

    public static String getStringValue(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public static void setStringValue(String key, String value) {
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static boolean getBooleanValue(String key) {
        return sp.getBoolean(key, false);
    }

    public static boolean getBooleanValue(String key, boolean value) {
        return sp.getBoolean(key, value);
    }

    public static void setBooleanValue(String key, boolean value) {
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static int getIntValue(String key,int def) {
        return sp.getInt(key, def);
    }

    public static void setLongValue(String key, long value) {
        Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLongValue(String key) {
        return sp.getLong(key, 0);
    }

    public static void setIntValue(String key, int value) {
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static float getFloatValue(String key) {
        return sp.getFloat(key, 0.0f);
    }

    public static void setFloatValue(String key, float value) {
        Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void clearAllValue(Context context) {
        SharedPreferences sharedData = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Editor editor = sharedData.edit();
        editor.clear();
        editor.apply();
    }




    private final String TAG_LANGUAGE = "language_select";
    private Locale systemCurrentLocal = Locale.ENGLISH;


    public void saveLanguage(int select) {
        setIntValue(TAG_LANGUAGE, select);
    }

    public int getSelectLanguage() {
        return getIntValue(TAG_LANGUAGE, 1);
    }

    public Locale getSystemCurrentLocal() {
        return systemCurrentLocal;
    }

    public void setSystemCurrentLocal(Locale local) {
        systemCurrentLocal = local;
    }


}
