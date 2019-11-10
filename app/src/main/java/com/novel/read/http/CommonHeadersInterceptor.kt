package com.novel.read.http

import com.common_lib.base.utils.SecurityUtils
import com.novel.read.constants.Constant
import com.novel.read.utlis.SpUtil

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Created by zlj on 2019/3/1.
 */
class CommonHeadersInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()

        val authKey = "Android"
        val timeStamp = (System.currentTimeMillis() / 1000).toString()
        val uid = SpUtil.getStringValue(Constant.Uid, "1")
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader("UID", uid)
        builder.addHeader("AUTHKEY", authKey)
        builder.addHeader("TIMESTAMP", timeStamp)

        builder.addHeader("SIGN", SecurityUtils.getInstance().MD5Decode(authKey + timeStamp).toUpperCase())

        return chain.proceed(builder.build())
    }
}
