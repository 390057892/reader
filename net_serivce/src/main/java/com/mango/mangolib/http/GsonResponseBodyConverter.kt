package com.mango.mangolib.http

import android.util.Log
import com.google.gson.Gson
import com.mango.mangolib.event.EventManager
import com.mango.mangolib.event.HTTPReponseErrorEvent
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.Type

/**
 * create by zlj on 2019/10/12
 * describe:
 */

class GsonResponseBodyConverter<T> internal constructor(
    private val gson: Gson,
    private val type: Type
) : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {
        val response = value.string()
        Log.e("GsonResponseBody", "response>>$response")
        //ResultResponse 只解析result字段
        val resultResponse = gson.fromJson(response, ErrorResponse::class.java)
        //result==0表示成功返回，继续用本来的Model类解析
        if (resultResponse.code == 1) {
            if (type === ErrorResponse::class.java) {
                return gson.fromJson<T>(response, type)
            }
            try {
                val jsonObject = JSONObject(response)
                    .getJSONObject("data")
                return gson.fromJson<T>(jsonObject.toString(), type)
            } catch (e: JSONException) {
                try {
                    val jsonArray = JSONObject(response)
                        .getJSONArray("data")
                    return gson.fromJson<T>(jsonArray.toString(), type)
                } catch (e1: JSONException) {
                    e.printStackTrace()
                    EventManager.instance.postEvent(HTTPReponseErrorEvent(resultResponse))
                }

            }

        } else {
            resultResponse.data = response
            EventManager.instance.postEvent(HTTPReponseErrorEvent(resultResponse))
        }
        return null
    }
}