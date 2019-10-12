package com.mango.mangolib.http

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * create by zlj on 2019/10/12
 * describe:
 */
class GsonRequestBodyConverter<T> internal constructor(
    private val gson: Gson,
    private val type: Type
) : Converter<T, RequestBody> {

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        try {
            gson.toJson(value, type, writer)
            writer.flush()
        } catch (e: IOException) {
            throw AssertionError(e) // Writing to Buffer does no I/O.
        }

        return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
    }

    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
        private val UTF_8 = Charset.forName("UTF-8")
    }
}
