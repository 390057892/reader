package com.novel.read.network.api

import com.novel.read.data.model.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface BookService {

    @POST("api/book/bookSearch")
    suspend fun searchBook(
        @Body body: RequestBody
    ): ApiResult<Pagination<SearchResp>>

    @POST("api/book/searchTermsList")
    suspend fun hotKey(): ApiResult<Pagination<String>>

    @POST("api/book/bookHome")
    suspend fun getHomeInfo(@Body body: RequestBody): ApiResult<HomeResp>

    @POST("api/book/bookDetail")
    suspend fun getBookInfo(@Body body: RequestBody):ApiResult<BookInfoResp>

    @POST("api/book/bookChapterList")
    suspend fun getDirectory(@Body body: RequestBody):ApiResult<ChapterResp>

    @POST("api/book/bookChapterDetail")
    suspend fun getChapterContent(@Body body: RequestBody):ApiResult<ChapterContentResp>

    @POST("api/book/typeList")
    suspend fun getChannelList(): ApiResult<ChannelResp>

    @POST("api/book/BookList")
    suspend fun getBookList(@Body body: RequestBody): ApiResult<Pagination<BookListResp>>

    @POST("api/book/bookRankQuery")
    suspend fun getBooRank(@Body body: RequestBody): ApiResult<Pagination<BookListResp>>

    @POST("api/book/similarRecommend")
    suspend fun getSimilarRecommend(@Body body: RequestBody): ApiResult<PaginationSimilar<BookListResp>>

    @POST("api/book/feedback")
    suspend fun feedback(@Body body: RequestBody): ApiResult<Default>

    @POST("api/edition/editionUpdate")
    suspend fun appUpdate(@Body body: RequestBody): ApiResult<AppUpdateResp>

    @Streaming
    @GET()
    suspend fun getImage(@Url fileUrl: String): ResponseBody
}