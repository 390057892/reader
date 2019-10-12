package com.common_lib.base.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

import com.common_lib.base.BaseConstants;


/**
 * Created by alex on 2016/3/2.
 */
public class NetworkUtils {
    public static final int TYPE_UNKNOWN = -1;

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        if (context == null) {
            return null;
        } else {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm == null ? null : cm.getActiveNetworkInfo();
        }
    }

    private static int getActiveNetworkType(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected() ? info.getType() : -1;
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected();
    }

    public static boolean isWiFiConnected(Context context) {
        return getActiveNetworkType(context) == 1;
    }

    @TargetApi(17)
    public static boolean isAirplaneModeOn(Context context) {
        return Build.VERSION.SDK_INT < 17 ? Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0 : Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public static boolean checkConnection(Context context) {
        if (isNetworkAvailable(context)) {
            return true;
        } else {
            ToastUtils.showNormalToast(context, BaseConstants.No_Internet);
            return false;
        }
    }
}
