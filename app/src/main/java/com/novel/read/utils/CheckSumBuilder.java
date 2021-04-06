package com.novel.read.utils;

import java.security.MessageDigest;
import java.util.Random;

/**
 * 功能说明：验证码生成工具类
 * 修改说明：
 * @author hu
 * @date 2021年4月6日 下午1:33:26
 * @version 0.1
 */
public class CheckSumBuilder {
    /**
     * 功能说明：计算并获取CheckSum
     * 修改说明：
     * @author hu
     * @date 2021年4月6日 下午1:33:26
     * @param appSecret 密码
     * @param nonce 随机串
     * @param curTime 当前时间戳
     * @return 返回生成的验证码
     */
    public static String getCheckSum(String appSecret, String nonce, String curTime) {
        return encode("sha1", appSecret + nonce + curTime);
    }

    /**
     * 功能说明：对参数进行MD5加密
     * 修改说明：
     * @author hu
     * @date 2021年4月6日 下午1:33:26
     * @param requestBody 要加密的内容
     * @return 返回MD5加密后的字符串
     */
    public static String getMD5(String requestBody) {
        return encode("md5", requestBody);
    }

    /**
     * 功能说明：使用指定加密算法对字符串进行加密
     * 修改说明：
     * @author hu
     * @date 2021年4月6日 下午1:33:26
     * @param algorithm 加密算法
     * @param value 要加密的字符串
     * @return 返回加密后的字符串
     */
    private static String encode(String algorithm, String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(value.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 功能说明：把字节数组格式化为16进制字符串
     * 修改说明：
     * @author hu
     * @date 2021年4月6日 下午1:33:26
     * @param bytes 字节数组
     * @return 返回格式化后的字符串
     */
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyz0123456789";
        Random random=new Random();
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<length;i++){
            int number=random.nextInt(36);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}

