package com.common_lib.base.utils;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zlj
 */
public class SecurityUtils {
    private static final String securityKey = "2E8PObAj8HI45Qu9";
    private MD5Utils md5Utils;
    private ObjectAnalysisUtils objUtils;

    private static SecurityUtils instance = null;

    private SecurityUtils() {
        md5Utils = MD5Utils.getInstance();
        objUtils = ObjectAnalysisUtils.getInstance();
    }


    public static synchronized SecurityUtils getInstance() {
        if (instance == null) {
            instance = new SecurityUtils();
        }
        return instance;
    }

    public String getSecurity(String itemName, String itemValue) {
        return MD5Decode(itemName + "=" + itemValue);
    }

    public String getSecurity(Object obj) {
        HashMap<String, String> attributeMap = objUtils.analysObject(obj, false);
        String sortedInfo = sortMap(attributeMap);
        Log.e("securit", sortedInfo);
        if (isEmpty(sortedInfo)) {
            return null;
        } else {
            return MD5Decode(sortedInfo);
        }
    }

    public String getSecurity(Object obj, Boolean needUserId) {
        HashMap<String, String> attributeMap = objUtils.analysObject(obj, needUserId);
        String sortedInfo = sortMap(attributeMap);
        if (isEmpty(sortedInfo)) {
            return null;
        } else {
            return MD5Decode(sortedInfo);
        }
    }

    public String getSecurity(HashMap<String, String> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        String sortedInfo = sortMap(map);
        if (isEmpty(sortedInfo)) {
            return null;
        } else {
            return MD5Decode(sortedInfo);
        }
    }


    private String sortMap(HashMap<String, String> map) {
        if (map == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Collection<String> keyset = map.keySet();
        List<String> list = new ArrayList<>(keyset);
        //对key键值按字典升序排序
        Collections.sort(list);

        for (String s : list) {
            sb.append(s);
            sb.append("=");
            sb.append(map.get(s));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


    public String MD5Decode(String info) {
        return md5Utils.getMD5Result(info + securityKey);
    }

    private boolean isEmpty(String info) {
        return info == null || info.isEmpty();
    }
}
