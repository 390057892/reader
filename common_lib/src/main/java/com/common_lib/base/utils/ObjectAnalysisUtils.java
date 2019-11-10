package com.common_lib.base.utils;


import android.util.Log;

import com.common_lib.base.GsonManager;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 这个类是用来解析Object属性的
 * Created by zlj
 */
public class ObjectAnalysisUtils {
    private static ObjectAnalysisUtils instance = null;

    private ObjectAnalysisUtils() {
    }

    public static synchronized ObjectAnalysisUtils getInstance() {
        if (instance == null) {
            instance = new ObjectAnalysisUtils();
        }
        return instance;
    }

    public HashMap<String, String> analysObject(Object obj, boolean needUId) {
        try {
            HashMap<String, String> result;
            if (needUId) {
                result = getClassInfo(obj, needUId);
            } else {
                result = getClassInfo(obj);
            }
            return result;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<String, String> getClassInfo(Object model) throws SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        HashMap<String, String> valueMap = new HashMap<>();
        // 获取实体类的所有属性，返回Field数组
        Field[] field = model.getClass().getDeclaredFields();
        // 获取属性的名字
        String[] modelName = new String[field.length];
        String[] modelType = new String[field.length];
        for (int i = 0; i < field.length; i++) {
            // 获取属性的名字
            String realName = field[i].getName();
            //这个是svn偷偷加上去的方法
            if (realName.startsWith("$")||realName.contains("serialVersionUID")){
                continue;
            }
            //这个方法就是为了获取security,这里就不对其进行处理啦,但是所有的带加密的类,其加密属性的名称都必修为security
            if (realName.equals("sign")) {
                continue;
            }
            //请求中的userId没有放在body里面.所以不需要用来计算签名
            if (realName.equals("userId")) {
                continue;
            }
            modelName[i] = realName;
            // 获取属性类型
            String type = field[i].getGenericType().toString();
            modelType[i] = type;
            //关键。。。可访问私有变量
            field[i].setAccessible(true);
            // 将属性的首字母大写
            String laterName = realName.replaceFirst(realName.substring(0, 1), realName.substring(0, 1)
                    .toUpperCase());
            Log.e("name",realName);
            Method m = model.getClass().getMethod("get" + laterName);
            switch (type) {
                case "class java.lang.String":
                    // 调用getter方法获取属性值
                    String valueString = (String) m.invoke(model);
                    if (valueString != null) {
                        valueMap.put(realName, valueString);
                    }
                    break;
                case "int":
                    int valueInt = (int) m.invoke(model);
                    if (valueInt != -1) {
                        valueMap.put(realName, valueInt + "");
                    }
                    break;
                case "class java.lang.Integer":
                    Integer valueInteger = (Integer) m.invoke(model);
                    if (valueInteger != null && valueInteger != -1) {
                        valueMap.put(realName, valueInteger.toString());
                    }
                    break;
                case "class java.lang.Long":
                    Long valueLong = (Long) m.invoke(model);
                    if (valueLong != null && valueLong != -1) {
                        valueMap.put(realName, valueLong.toString());
                    }
                    break;
                case "class java.lang.Short":
                    Short valueShort = (Short) m.invoke(model);
                    if (valueShort != null) {
                        valueMap.put(realName, valueShort.toString());
                    }
                    break;
                case "class java.lang.Double":
                    Double valueDouble = (Double) m.invoke(model);
                    if (valueDouble != null) {
                        valueMap.put(realName, valueDouble.toString());
                    }
                    break;
                case "class java.lang.Boolean":
                    Boolean valueBoolean = (Boolean) m.invoke(model);
                    if (valueBoolean != null) {
                        valueMap.put(realName, valueBoolean.toString());
                    }
                    break;
                case "class java.util.Date":
                    Date valueData = (Date) m.invoke(model);
                    if (valueData != null) {
                        valueMap.put(realName, valueData.toString());
                    }
                    break;
                case "java.util.ArrayList<java.lang.String>":
                    ArrayList<String> valueAList = (ArrayList<String>) m.invoke(model);
                    if (valueAList != null) {
                        valueMap.put(realName, getListValue(valueAList));
                    }
                    break;
                case "java.util.List<java.lang.String>":
                    List<String> valueList = (List<String>) m.invoke(model);
                    if (valueList != null) {
                        valueMap.put(realName, getListValue(valueList));
                    }
                    break;
                default:
                    if (m.invoke(model)==null){
                        continue;
                    }else{
                        Gson gson = GsonManager.Companion.getInstance().getGson();
                        String value = gson.toJson(m.invoke(model));
                        valueMap.put(realName, value);
                    }

            }
        }
        return valueMap;
    }

    private HashMap<String, String> getClassInfo(Object model, boolean needUId) throws SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        HashMap<String, String> valueMap = new HashMap<>();
        // 获取实体类的所有属性，返回Field数组
        Field[] field = model.getClass().getDeclaredFields();
        // 获取属性的名字
        String[] modelName = new String[field.length];
        String[] modelType = new String[field.length];
        for (int i = 0; i < field.length; i++) {
            // 获取属性的名字
            String realName = field[i].getName();
            //这个是svn偷偷加上去的方法
            //这个是svn偷偷加上去的方法
            if (realName.startsWith("$")||realName.contains("serialVersionUID")){
                continue;
            }
            //这个方法就是为了获取security,这里就不对其进行处理啦,但是所有的带加密的类,其加密属性的名称都必修为security
            if (realName.equals("sign")) {
                continue;
            }
            //请求中的userId没有放在body里面.所以不需要用来计算签名
            if (!needUId && realName.equals("userId")) {
                continue;
            }
            modelName[i] = realName;
            // 获取属性类型
            String type = field[i].getGenericType().toString();
            modelType[i] = type;
            //关键。。。可访问私有变量
            field[i].setAccessible(true);
            // 将属性的首字母大写
            String laterName = realName.replaceFirst(realName.substring(0, 1), realName.substring(0, 1)
                    .toUpperCase());
            Method m = model.getClass().getMethod("get" + laterName);
            switch (type) {
                case "class java.lang.String":
                    // 调用getter方法获取属性值
                    String valueString = (String) m.invoke(model);
                    if (valueString != null) {
                        valueMap.put(realName, valueString);
                    }
                    break;
                case "int":
                    int valueInt = (int) m.invoke(model);
                    if (valueInt != -1) {
                        valueMap.put(realName, valueInt + "");
                    }
                    break;
                case "class java.lang.Integer":
                    Integer valueInteger = (Integer) m.invoke(model);
                    if (valueInteger != null && valueInteger != -1) {
                        valueMap.put(realName, valueInteger.toString());
                    }
                    break;
                case "class java.lang.Long":
                    Long valueLong = (Long) m.invoke(model);
                    if (valueLong != null && valueLong != -1) {
                        valueMap.put(realName, valueLong.toString());
                    }
                    break;
                case "class java.lang.Short":
                    Short valueShort = (Short) m.invoke(model);
                    if (valueShort != null) {
                        valueMap.put(realName, valueShort.toString());
                    }
                    break;
                case "class java.lang.Double":
                    Double valueDouble = (Double) m.invoke(model);
                    if (valueDouble != null) {
                        valueMap.put(realName, valueDouble.toString());
                    }
                    break;
                case "class java.lang.Boolean":
                    Boolean valueBoolean = (Boolean) m.invoke(model);
                    if (valueBoolean != null) {
                        valueMap.put(realName, valueBoolean.toString());
                    }
                    break;
                case "class java.util.Date":
                    Date valueData = (Date) m.invoke(model);
                    if (valueData != null) {
                        valueMap.put(realName, valueData.toString());
                    }
                    break;
                case "java.util.ArrayList<java.lang.String>":
                    ArrayList<String> valueAList = (ArrayList<String>) m.invoke(model);
                    if (valueAList != null) {
                        valueMap.put(realName, getListValue(valueAList));
                    }
                    break;
                case "java.util.List<java.lang.String>":
                    List<String> valueList = (List<String>) m.invoke(model);
                    if (valueList != null) {
                        valueMap.put(realName, getListValue(valueList));
                    }
                    break;
                default:
                    Gson gson = GsonManager.Companion.getInstance().getGson();
                    String value = gson.toJson(m.invoke(model));
                    valueMap.put(realName, value);
            }
        }
        return valueMap;
    }


    private String getListValue(List<String> info) {
        StringBuilder sb = new StringBuilder();
        for (String s : info) {
            sb.append(s);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String getListValue(ArrayList<String> info) {
        StringBuilder sb = new StringBuilder();
        for (String s : info) {
            sb.append(s);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

//    private String getListValue(ArrayList<String> info) {
//        Gson gson=GsonManager.getInstance().getGson();
//        return gson.toJson(info);
//    }

    private String getObjectValue(Object obj) {
        Gson gson = GsonManager.Companion.getInstance().getGson();
        return gson.toJson(obj);
    }

}
