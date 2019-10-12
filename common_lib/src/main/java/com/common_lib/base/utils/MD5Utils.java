package com.common_lib.base.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by alex on 16/7/4.
 */
public class MD5Utils {
    private static MD5Utils instance = null;

    private MD5Utils() {
    }

    public static synchronized MD5Utils getInstance() {
        if (instance == null) {
            instance = new MD5Utils();
        }
        return instance;
    }

    public String getMD5Result(String info){
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(info.getBytes("UTF-8"));
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++){
                if (Integer.toHexString(0xFF & messageDigest[i]).length() == 1)
                    hexString.append("0").append(Integer.toHexString(0xFF & messageDigest[i]));
                else
                    hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }
}
