package com.novel.read.http

import com.google.gson.GsonBuilder
import com.mango.mangolib.http.GsonUTCdateAdapter
import com.mango.mangolib.http.MyRequestType
import com.mango.mangolib.http.ResponseConverterFactory

import java.util.Date
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object ServiceGenerator {
    private const val API_BASE_URL_TEXT = "http://novel.duoduvip.com/"

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Date::class.java, GsonUTCdateAdapter()).create()


    private val builderTEXT = Retrofit.Builder()
        .baseUrl(API_BASE_URL_TEXT)
        .client(okHttp)
        .addConverterFactory(ResponseConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))

    private val okHttp: OkHttpClient
        get() = OkHttpClient()
            .newBuilder()
            .addInterceptor(CommonHeadersInterceptor())
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

    fun <S> createService(serviceClass: Class<S>, type: MyRequestType): S {
        val retrofit = builderTEXT.build()
        return retrofit.create(serviceClass)
    }

    fun formatResponse(obj: Any): String {
        return gson.toJson(obj)
    }
}
