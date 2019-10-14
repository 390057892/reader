package com.novel.read.utlis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.UUID;

public class PhoneUtils {
    private TelephonyManager telephonemanager;
    private String IMSI;
    private Context ctx;

    /**
     * 获取手机国际识别码IMEI
     */
    public PhoneUtils(Context context) {
        ctx = context;
        telephonemanager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取手机号码
     */
    @SuppressLint("MissingPermission")
    public String getNativePhoneNumber() {

        String nativephonenumber = null;
        nativephonenumber = telephonemanager.getLine1Number();

        return nativephonenumber;
    }

    /**
     * 获取手机服务商信息
     */
    @SuppressLint("MissingPermission")
    public String getProvidersName() {
        String providerName = null;
        try {
            IMSI = telephonemanager.getSubscriberId();
            //IMSI前面三位460是国家号码，其次的两位是运营商代号，00、02是中国移动，01是联通，03是电信。
            System.out.print("IMSI是：" + IMSI);
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                providerName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                providerName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                providerName = "中国电信";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return providerName;

    }


    public static String getUniquePsuedoID() {
        String serial = null;
        String m_szDevIDShort = "35" + // 35是IMEI开头的号
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                + Build.CPU_ABI.length() % 10
                + Build.DEVICE.length() % 10
                + Build.DISPLAY.length() % 10
                + Build.HOST.length() % 10
                + Build.ID.length() % 10
                + Build.MANUFACTURER.length() % 10
                + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10;
        //13 位
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {//serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 获取手机信息
     */
    @SuppressLint("MissingPermission")
    public String getPhoneInfo() {

        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
//        return ("\nDeviceID(IMEI)" + tm.getDeviceId()) +
//                "\nDeviceSoftwareVersion:" + tm.getDeviceSoftwareVersion() +
//                "\ngetLine1Number:" + tm.getLine1Number() +
//                "\nNetworkCountryIso:" + tm.getNetworkCountryIso() +
//                "\nNetworkOperator:" + tm.getNetworkOperator() +
//                "\nNetworkOperatorName:" + tm.getNetworkOperatorName() +
//                "\nNetworkType:" + tm.getNetworkType() +
//                "\nPhoneType:" + tm.getPhoneType() +
//                "\nSimCountryIso:" + tm.getSimCountryIso() +
//                "\nSimOperator:" + tm.getSimOperator() +
//                "\nSimOperatorName:" + tm.getSimOperatorName() +
//                "\nSimSerialNumber:" + tm.getSimSerialNumber() +
//                "\ngetSimState:" + tm.getSimState() +
//                "\nSubscriberId:" + tm.getSubscriberId() +
//                "\nVoiceMailNumber:" + tm.getVoiceMailNumber();

    }
}
