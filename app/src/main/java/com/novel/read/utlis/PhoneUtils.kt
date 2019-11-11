package com.novel.read.utlis

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import java.util.*

/**
 * 获取手机国际识别码IMEI
 */
class PhoneUtils(private val ctx: Context) {

    private val telephonemanager: TelephonyManager = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private var IMSI: String? = null

    /**
     * 获取手机号码
     */
    val nativePhoneNumber: String?
        @SuppressLint("MissingPermission")
        get() {

            var nativephonenumber: String? = null
            nativephonenumber = telephonemanager.line1Number

            return nativephonenumber
        }

    /**
     * 获取手机服务商信息
     */
    //IMSI前面三位460是国家号码，其次的两位是运营商代号，00、02是中国移动，01是联通，03是电信。
    val providersName: String?
        @SuppressLint("MissingPermission")
        get() {
            var providerName: String? = null
            try {
                IMSI = telephonemanager.subscriberId
                print("IMSI是：" + IMSI!!)
                if (IMSI!!.startsWith("46000") || IMSI!!.startsWith("46002")) {
                    providerName = "中国移动"
                } else if (IMSI!!.startsWith("46001")) {
                    providerName = "中国联通"
                } else if (IMSI!!.startsWith("46003")) {
                    providerName = "中国电信"
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return providerName

        }

    /**
     * 获取手机信息
     */
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
    val phoneInfo: String
        @SuppressLint("MissingPermission")
        get() {

            val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.deviceId

        }

    companion object {

        // 35是IMEI开头的号
        //13 位
        //API>=9 使用serial号
        //serial需要一个初始化
        // 随便一个初始化
        //使用硬件信息拼凑出来的15位号码
        val uniquePsuedoID: String
            get() {
                var serial: String? = null
                val m_szDevIDShort = ("35" +
                        Build.BOARD.length % 10 + Build.BRAND.length % 10
                        + Build.CPU_ABI.length % 10
                        + Build.DEVICE.length % 10
                        + Build.DISPLAY.length % 10
                        + Build.HOST.length % 10
                        + Build.ID.length % 10
                        + Build.MANUFACTURER.length % 10
                        + Build.MODEL.length % 10 + Build.PRODUCT.length % 10
                        + Build.TAGS.length % 10 + Build.TYPE.length % 10 +
                        Build.USER.length % 10)
                try {
                    serial = Build::class.java.getField("SERIAL").get(null).toString()
                    return UUID(
                        m_szDevIDShort.hashCode().toLong(),
                        serial.hashCode().toLong()
                    ).toString()
                } catch (exception: Exception) {
                    serial = "serial"
                }

                return UUID(
                    m_szDevIDShort.hashCode().toLong(),
                    serial!!.hashCode().toLong()
                ).toString()
            }
    }
}
