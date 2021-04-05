package com.novel.read.network.api

import androidx.annotation.Keep

@Keep
data class ApiResult<T>(
    val code: Int,
    val message: String,
    private val data: T?
) {
    fun apiData(): T {
        if (code == 200 && data != null) {
            return data
        } else {
            throw ApiException(code, message)
        }
    }
}
