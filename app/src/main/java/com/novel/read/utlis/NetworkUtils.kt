package com.novel.read.utlis

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

import com.novel.read.base.MyApp

object NetworkUtils {

    /**
     * 获取活动网络信息
     * @return NetworkInfo
     */
    private val networkInfo: NetworkInfo?
        get() {
            val cm = MyApp.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }

    /**
     * 网络是否可用
     * @return
     */
    val isAvailable: Boolean
        get() {
            val info = networkInfo
            return info != null && info.isAvailable
        }

    /**
     * 网络是否连接
     * @return
     */
    val isConnected: Boolean
        get() {
            val info = networkInfo
            return info != null && info.isConnected
        }

    /**
     * 判断wifi是否连接状态
     *
     * 需添加权限 `<uses-permission android:name="android.permission
     * .ACCESS_NETWORK_STATE"/>`
     *
     * @param context 上下文
     * @return `true`: 连接<br></br>`false`: 未连接
     */
    fun isWifiConnected(context: Context): Boolean {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return (cm.activeNetworkInfo != null
                && cm.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI)
    }


}
