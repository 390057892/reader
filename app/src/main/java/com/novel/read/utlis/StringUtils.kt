package com.novel.read.utlis

import android.text.TextUtils
import com.novel.read.widget.page.ReadSettingManager.Companion.SHARED_READ_CONVERT_TYPE

import com.spreada.utils.chinese.ZHConverter

object StringUtils {

    /**
     * 将文本中的半角字符，转换成全角字符
     */
    fun halfToFull(input: String): String {
        val text = deleteImgs(input)
        val c = text.toCharArray()
        for (i in c.indices) {
            if (c[i].toInt() == 32)
            //半角空格
            {
                c[i] = 12288.toChar()
                continue
            }
            //根据实际情况，过滤不需要转换的符号
            //if (c[i] == 46) //半角点号，不转换
            // continue;

            if (c[i].toInt() in 33..126)
            //其他符号都转换为全角
                c[i] = (c[i].toInt() + 65248).toChar()
        }
        return String(c)
    }

    private fun deleteImgs(content: String?): String {
        return if (content != null && !TextUtils.isEmpty(content)) {
            // 去掉所有html元素,
            var str =
                content.replace("&[a-zA-Z]{1,10};".toRegex(), "").replace("<[^>]*>".toRegex(), "")
            str = str.replace("[(/>)<]".toRegex(), "")
            str
        } else {
            ""
        }
    }

    fun delete160(des: String): String {
        var text = des
        text = text.replace("&#160;".toRegex(), "")
        text = text.replace("&amp;#160;".toRegex(), "")
        text = text.replace("\\s*".toRegex(), "")
        text = text.trim { it <= ' ' }
        return text
    }

    //繁簡轉換
    fun convertCC(input: String): String {
        val convertType = SpUtil.getIntValue(SHARED_READ_CONVERT_TYPE, 1)

        if (input.isEmpty())
            return ""

        return if (convertType != 0) ZHConverter.getInstance(ZHConverter.TRADITIONAL).convert(input) else input
    }

}
