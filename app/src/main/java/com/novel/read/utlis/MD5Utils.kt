package com.novel.read.utlis

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

/**
 * 将字符串转化为MD5
 */

object MD5Utils {

    private fun strToMd5By32(str: String): String? {
        var reStr: String? = null
        try {
            val md5 = MessageDigest.getInstance("MD5")
            val bytes = md5.digest(str.toByteArray())
            val stringBuffer = StringBuilder()
            for (b in bytes) {
                val bt = b and 0xff.toByte()
                if (bt < 16) {
                    stringBuffer.append(0)
                }
                stringBuffer.append(Integer.toHexString(bt.toInt()))
            }
            reStr = stringBuffer.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return reStr
    }

    fun strToMd5By16(str: String): String? {
        var reStr = strToMd5By32(str)
        if (reStr != null) {
            reStr = reStr.substring(8, 24)
        }
        return reStr
    }
}
