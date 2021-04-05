package com.novel.read.network.repository

import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.data.model.ChapterContentResp
import com.novel.read.data.model.LoginReq
import com.novel.read.network.ServiceCreator
import com.novel.read.utils.ext.GSON
import com.novel.read.help.coroutine.Coroutine
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.RequestBody
import kotlin.coroutines.CoroutineContext

class BookRepository {

    suspend fun getChannelList() = withContext(Dispatchers.IO) {
        ServiceCreator.apiService.getChannelList().apiData()
    }

    suspend fun getBookInfo(bookId: Long) = withContext(Dispatchers.IO) {
        val map = HashMap<String, Any>()
        map["bookId"] = bookId
        ServiceCreator.apiService.getBookInfo(mapToBody(map)).apiData()
    }

    suspend fun getDirectory(bookId: Long) = withContext(Dispatchers.IO) {
        val map = HashMap<String, Any>()
        map["bookId"] = bookId
        ServiceCreator.apiService.getDirectory(mapToBody(map)).apiData()
    }


    fun getContents(
        scope: CoroutineScope,
        book: Book,
        bookChapter: BookChapter,
        context: CoroutineContext = Dispatchers.IO
    ): Coroutine<ChapterContentResp> {
        return Coroutine.async(scope, context) {
            val map = HashMap<String, Any>()
            map["bookId"] = book.bookId
            map["chapterId"] = bookChapter.chapterId
            getChapterContent(book.bookId, bookChapter.chapterId)
        }
    }

    private suspend fun getChapterContent(bookId: Long, chapterId: Long): ChapterContentResp {
        val map = HashMap<String, Any>()
        map["bookId"] = bookId
        map["chapterId"] = chapterId
        return ServiceCreator.apiService.getChapterContent(mapToBody(map)).apiData()
    }


    suspend fun getBookList(channelId: Long, pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            val map = HashMap<String, Any>()
            map["bookTypeId"] = channelId.toString()
            map["pageNum"] = pageNum.toString()
            map["pageSize"] = pageSize.toString()
            ServiceCreator.apiService.getBookList(mapToBody(map)).apiData()
        }

    suspend fun getDailyList(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            val map = HashMap<String, Any>()
            map["hotStatus"] = "1"
            map["pageNum"] = pageNum.toString()
            map["pageSize"] = pageSize.toString()
            ServiceCreator.apiService.getBookList(mapToBody(map)).apiData()
        }

    suspend fun getEndList(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            val map = HashMap<String, Any>()
            map["endStatus"] = "1"
            map["pageNum"] = pageNum.toString()
            map["pageSize"] = pageSize.toString()
            ServiceCreator.apiService.getBookList(mapToBody(map)).apiData()
        }


    suspend fun getBooRank(pageNum: Int, pageSize: Int, rankType: Int) =
        withContext(Dispatchers.IO) {
            val map = HashMap<String, Any>()
            map["rankType"] = rankType.toString()
            map["pageNum"] = pageNum.toString()
            map["pageSize"] = pageSize.toString()
            ServiceCreator.apiService.getBooRank(mapToBody(map)).apiData()
        }

    suspend fun getSimilarRecommend(bookTypeId: Int) =
        withContext(Dispatchers.IO) {
            val map = HashMap<String, Any>()
            map["bookTypeId"] = bookTypeId
            ServiceCreator.apiService.getSimilarRecommend(mapToBody(map)).apiData()
        }

    suspend fun feedback(content: String) =
        withContext(Dispatchers.IO) {
            val map = HashMap<String, Any>()
            map["content"] = content
            ServiceCreator.apiService.feedback(mapToBody(map)).apiData()
        }

    suspend fun login(loginReq: LoginReq) = withContext(Dispatchers.IO) {
        ServiceCreator.apiService.googleLogin(loginReq).apiData()
    }

    suspend fun getGoods() = withContext(Dispatchers.IO) {
        ServiceCreator.apiService.getGoods().apiData()
    }

    suspend fun buyVip() = withContext(Dispatchers.IO) {
        ServiceCreator.apiService.buyVip().apiData()
    }

    private fun mapToBody(map: HashMap<String, Any>): RequestBody {
        return RequestBody.create(
            MediaType.parse("application/json;charset=UTF-8"),
            GSON.toJson(map)
        )
    }

}