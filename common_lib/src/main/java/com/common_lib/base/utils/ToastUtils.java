package com.common_lib.base.utils;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by alex on 2016/3/2.
 */
public class ToastUtils {
    public enum Duration {SHORT, LONG}

    private ToastUtils() {
        throw new AssertionError();
    }

    public static void showNormalToast(Context context, int stringResId) {
        showToast(context, stringResId, Duration.SHORT);
    }

    public static void showNormalToast(Context context, String text) {
        showToast(context, text, Duration.SHORT);
    }

    public static void showLongDurationToast(Context context, int stringResId) {
        showToast(context, stringResId, Duration.LONG);
    }

    public static void showLongDurationToast(Context context, String text) {
        showToast(context, text, Duration.LONG);
    }


    private static void showToast(Context context, int text, Duration duration) {
        Toast toast = Toast.makeText(context, text,
                (duration == Duration.SHORT ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG));
//        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private static void showToast(Context context, String text, Duration duration) {
        Toast toast = Toast.makeText(context, text,
                (duration == Duration.SHORT ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG));
//        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

}
