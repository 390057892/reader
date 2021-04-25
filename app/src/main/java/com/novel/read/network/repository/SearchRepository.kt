package com.novel.read.network.repository

import com.novel.read.network.ServiceCreator
import com.novel.read.utils.ext.GSON
import com.spreada.utils.chinese.ZHConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody

class SearchRepository {



    suspend fun searchBook(page: Int, pageSize: Int, key: String) = withContext(Dispatchers.IO) {
        val map = HashMap<String, String>()
        val key1 = ZHConverter.getInstance(ZHConverter.SIMPLIFIED).convert(key)
        map["pageNum"] = page.toString()
        map["pageSize"] = pageSize.toString()
        map["searchTerms"] = key1
        val body: RequestBody =
            RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), GSON.toJson(map))
        val books = ServiceCreator.apiService.searchBook(body).apiData()
        books
    }

    suspend fun hotKey() = withContext(Dispatchers.IO) {
        val books = ServiceCreator.apiService.hotKey().apiData()
        books
    }


}