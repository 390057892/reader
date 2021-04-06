package com.novel.read.network

import android.util.Log
import com.novel.read.constant.AppConst
import com.novel.read.data.model.CheckSumDTO
import com.novel.read.network.api.BookService
import com.novel.read.utils.CheckSumBuilder
import com.novel.read.utils.ext.MYGSON
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws


object ServiceCreator {

    private const val BASE_URL = "http://172.104.61.64:8009/"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(LoggingInterceptor())
        .addInterceptor(HeaderInterceptor())

    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient.build())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())


    private val retrofit = builder.build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    /**ApiService*/
    val apiService: BookService = retrofit.create(BookService::class.java)

    private val builder1 = Retrofit.Builder()
        .baseUrl("http://yijianda8.com/")
        .client(httpClient.build())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit1 = builder1.build()
    val apiService1: BookService = retrofit1.create(BookService::class.java)

    class LoggingInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val t1 = System.nanoTime()
            Log.e(TAG, "request: ${request.url()} \n ${getRequestInfo(request)}")

            val response = chain.proceed(request)

            val t2 = System.nanoTime()
            Log.e(
                TAG,
                "response for  ${response.request()
                    .url()} in ${(t2 - t1) / 1e6} ms\n${getResponseInfo(response)}"
            )
            return response
        }

        /**
         * 打印返回消息
         * @param response 返回的对象
         */
        private fun getResponseInfo(response: Response?): String? {
            var str = ""
            if (response == null || !response.isSuccessful) {
                return str
            }
            val responseBody = response.body()
            val contentLength = responseBody!!.contentLength()
            val source = responseBody.source()
            try {
                source.request(Long.MAX_VALUE) // Buffer the entire body.
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val buffer: Buffer = source.buffer
            val charset: Charset = Charset.forName("utf-8")
            if (contentLength != 0L) {
                str = buffer.clone().readString(charset)
            }
            return str
        }

        /**
         * 打印请求体
         * @param request 请求的对象
         */
        private fun getRequestInfo(request: Request?): String? {
            val requestBody = request!!.body()
            val buffer = Buffer()
            try {
                if(requestBody!=null) {
                    requestBody.writeTo(buffer)
                }else{
                    return ""
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return ""
            }

            var charset = Charset.forName("UTF-8")
            val contentType = requestBody!!.contentType()
            if (contentType != null) {
                charset = contentType.charset(Charset.forName("UTF-8"))
            }

            return buffer.readString(charset)
        }

        companion object {
            const val TAG = "LoggingInterceptor"
        }
    }

    class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val time = System.currentTimeMillis().toString()
            val nonce = CheckSumBuilder.getRandomString(8)
            val checkSum = CheckSumBuilder.getCheckSum(
                AppConst.AppSecret,
                nonce,
                time
            )
            val checkSha = MYGSON.toJson(CheckSumDTO(AppConst.AppId,nonce,time,checkSum))
            val request = original.newBuilder().apply {
                header("model", "Android")
                header("checkSumDTO", checkSha)
                header("If-Modified-Since", URLEncoder.encode("${Date()}", "utf-8"))
                header("User-Agent", System.getProperty("http.agent") ?: "unknown")
            }.build()
            return chain.proceed(request)
        }
    }

}