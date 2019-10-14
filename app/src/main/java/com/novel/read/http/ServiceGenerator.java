package com.novel.read.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mango.mangolib.http.GsonUTCdateAdapter;
import com.mango.mangolib.http.MyRequestType;
import com.mango.mangolib.http.ResponseConverterFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {
    private static final String API_BASE_URL_TEXT = "http://novel.duoduvip.com/";

    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Date.class, new GsonUTCdateAdapter()).create();


    private static Retrofit.Builder builderTEXT = new Retrofit.Builder()
            .baseUrl(API_BASE_URL_TEXT)
            .client(getOkHttp())
            .addConverterFactory(ResponseConverterFactory.Companion.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson));

    private static OkHttpClient getOkHttp(){
        return new OkHttpClient()
                .newBuilder()
                .addInterceptor(new CommonHeadersInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private ServiceGenerator() {
    }

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, final MyRequestType type) {
        Retrofit retrofit = builderTEXT.build();
        return retrofit.create(serviceClass);
    }


    public static String formatResponse(Object obj) {
        return gson.toJson(obj);
    }
}
