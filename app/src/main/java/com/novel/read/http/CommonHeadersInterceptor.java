package com.novel.read.http;
import androidx.annotation.NonNull;

import com.common_lib.base.utils.SecurityUtils;
import com.novel.read.constants.Constant;
import com.novel.read.utlis.SpUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zlj on 2019/3/1.
 */
public class CommonHeadersInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();

        String authKey = "Android";
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String uid = SpUtil.getStringValue(Constant.Uid, "1");
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("UID", uid);
        builder.addHeader("AUTHKEY", authKey);
        builder.addHeader("TIMESTAMP", timeStamp);

        builder.addHeader("SIGN", SecurityUtils.getInstance().MD5Decode(authKey + timeStamp).toUpperCase());


        return chain.proceed(builder.build());
    }
}
