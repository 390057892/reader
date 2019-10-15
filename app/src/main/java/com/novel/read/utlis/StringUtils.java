package com.novel.read.utlis;

import android.content.Context;
import android.text.TextUtils;

import com.spreada.utils.chinese.ZHConverter;

import static com.novel.read.widget.page.ReadSettingManager.SHARED_READ_CONVERT_TYPE;

public class StringUtils {

    /**
     * 将文本中的半角字符，转换成全角字符
     *
     * @param input
     * @return
     */
    public static String halfToFull(String input) {
        input = deleteImgs(input);
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) //半角空格
            {
                c[i] = (char) 12288;
                continue;
            }
            //根据实际情况，过滤不需要转换的符号
            //if (c[i] == 46) //半角点号，不转换
            // continue;

            if (c[i] > 32 && c[i] < 127)    //其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    private static String deleteImgs(String content) {
        if (content!=null&&!TextUtils.isEmpty(content)){
            // 去掉所有html元素,
            String str = content.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
                    "<[^>]*>", "");
            str = str.replaceAll("[(/>)<]", "");
            return str;
        }else {
            return "";
        }
    }

    public static String delete160(String des) {
        des = des.replaceAll("&#160;", "");
        des = des.replaceAll("&amp;#160;", "");
        des = des.replaceAll("\\s*", "");
        des = des.trim();
        return des;
    }

    //繁簡轉換
    public static String convertCC(String input) {
        int convertType = SpUtil.getIntValue(SHARED_READ_CONVERT_TYPE, 1);

        if (input.length() == 0)
            return "";

        return (convertType != 0) ? ZHConverter.getInstance(ZHConverter.TRADITIONAL).convert(input) : input;
    }

}
