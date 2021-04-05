package com.novel.read.network.repository

import com.novel.read.App
import com.novel.read.constant.AppConst
import com.novel.read.network.ServiceCreator
import com.novel.read.utils.ext.GSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody

class HomeRepository {


    suspend fun getHomeBook(bookClass: Int) = withContext(Dispatchers.IO) {
        val map = HashMap<String, Any>()
        val books = ServiceCreator.apiService.getHomeInfo(mapToBody(map)).apiData()
        books
    }

    suspend fun appUpdate() = withContext(Dispatchers.IO) {
        val map = HashMap<String, Any>()
        map["shellName"] = AppConst.shellName
        map["editionCode"] = App.versionName
        ServiceCreator.apiService.appUpdate(mapToBody(map)).apiData()
    }

    private fun mapToBody(map: HashMap<String, Any>): RequestBody {
        return RequestBody.create(
            MediaType.parse("application/json;charset=UTF-8"),
            GSON.toJson(map)
        )
    }

}