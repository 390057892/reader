package com.common_lib.base.utils;

import com.common_lib.base.BaseConstants;
import com.common_lib.base.GsonManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于处理一些奇奇怪怪的String类型转换的工具类
 * Created by alex on 16/7/20.
 */
public class StringUtils {
    //这个方法是用来处理服务端返回的url列表的
    public static List<String> formatStringUrl(String imageContent) {
        List<String> res = null;
        if (imageContent == null || imageContent.isEmpty()) {
            return res;
        }
        Gson gson = GsonManager.Companion.getInstance().getGson();
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        res = gson.fromJson(imageContent, listType);
        return res;
    }


    //这个方法是获取url列表的方法,这个方法就是
    public static List<String> formatStringUrl(String imageContent, boolean addHead, int maxLength) {
        List<String> res;
        if (imageContent == null || imageContent.isEmpty()) {
            res = new ArrayList<>();
            res.add(null);
            return res;
        }
        Gson gson = GsonManager.Companion.getInstance().getGson();
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        res = gson.fromJson(imageContent, listType);
        if (res.size() > 0 && res.size() < maxLength) {
            if (addHead && res.get(res.size()-1) != null) {
                res.add(res.size(), null);
            }
        } else if(res.size()==maxLength){
            return res;
        }else{
            res = new ArrayList<>();
            res.add(null);
        }
        return res;
    }

    //这是将一个字符串的长度增大
    public static String increaseGroupMessage(String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append(msg);
        while (builder.length() < 100) {
            builder.append(BaseConstants.EMPTY_STRING);
        }
        return builder.toString();
    }

    public static String formatList(List<String> info) {
        String res = null;
        List<String> infoList = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            if (info.get(i) != null) {
                infoList.add(info.get(i));
            }
        }
        if (infoList.size() > -1) {
            Gson gson = GsonManager.Companion.getInstance().getGson();
            res = gson.toJson(infoList);
        }
        return res;
    }

    //从图片的路径当中获取图片的名称
    public static String getFileNameFromPath(String path) {
        String res;
        String[] s = path.split("/");
        res = s[s.length - 1];
        return res;
    }






}
