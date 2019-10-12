package com.common_lib.base.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by alex on 2017/2/8.
 */

public class DeviceInfoUtil {
    public static String getDeviceInfo(Context context){
        String A_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();
        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        sb.append("\nNetworkType = " + tm.getNetworkType());
        sb.append("\nPhoneType = " + tm.getPhoneType());
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSimState = " + tm.getSimState());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        sb.append("\nSoftwareVersion=" + tm.getDeviceSoftwareVersion());
        sb.append("\nCPU="+getCPUSerial());
        sb.append("\nBoard="+ Build.BOARD);
        sb.append("\nCPU1="+ Build.CPU_ABI);
        sb.append("\nDEVICE="+ Build.DEVICE);
        sb.append("\nHARDWARE="+ Build.HARDWARE);
        sb.append("\nANDROID_ID = " + A_ID);
        return sb.toString();
    }

    /**
     * 获取CPU序列号
     *
     * @return CPU序列号(16位)
     * 读取失败为"0000000000000000"
     */
    private static String getCPUSerial() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        try {
            //读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            //查找CPU序列号
            str = input.readLine();
            if (str != null) {
                //查找到序列号所在行
                if (str.indexOf("Serial") > -1) {
                    //提取序列号
                    strCPU = str.substring(str.indexOf(":") + 1,
                            str.length());
                    //去空格
                    cpuAddress = strCPU.trim();
                }
            } else {
            }
        } catch (IOException ex) {
            //赋予默认值
            ex.printStackTrace();
        }
        return cpuAddress;
    }
}
