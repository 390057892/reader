package com.mango.mangolib.http

import androidx.annotation.Nullable
import com.common_lib.base.GsonManager
import com.google.gson.Gson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * create by zlj on 2019/10/12
 * describe:
 */
class ResponseConverterFactory private constructor(gson: Gson?) : Converter.Factory() {

    private val gson: Gson?

    init {
        var gson = gson
        if (gson == null) {
            gson = GsonManager.getInstance().getGson()
        }
        this.gson = gson
    }

    @Nullable
    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return GsonResponseBodyConverter<Any>(gson!!, type!!)
    }

    @Nullable
    override fun requestBodyConverter(
        type: Type?,
        parameterAnnotations: Array<Annotation>?,
        methodAnnotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<*, RequestBody>? {
        return GsonRequestBodyConverter<Any>(gson!!, type!!)
    }

    companion object {
        /**
         * Create an instance using `gson` for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        @JvmOverloads
        fun create(gson: Gson = GsonManager.getInstance().getGson()!!): ResponseConverterFactory {
            return ResponseConverterFactory(gson)
        }
    }
}
