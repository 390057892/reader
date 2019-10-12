package com.mango.mangolib.http

import com.google.gson.*
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * create by zlj on 2019/10/12
 * describe:
 */
class GsonUTCdateAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {

    private val dateFormat: DateFormat

    init {
        dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CHINA)      //This is the format I need
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")                               //This is the key line which converts the date to UTC which cannot be accessed with the default serializer
    }

    @Synchronized
    override fun serialize(date: Date, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        return JsonPrimitive(dateFormat.format(date))
    }

    @Synchronized
    override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): Date {
        try {
            return dateFormat.parse(jsonElement.asString)
        } catch (e: ParseException) {
            throw JsonParseException(e)
        }
    }

}
