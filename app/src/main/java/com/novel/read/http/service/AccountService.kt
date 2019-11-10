package com.novel.read.http.service

import com.mango.mangolib.http.ErrorResponse
import com.novel.read.model.protocol.BookArticleResp
import com.novel.read.model.protocol.BookDetailResp
import com.novel.read.model.protocol.CategoryTypeResp
import com.novel.read.model.protocol.ChapterInfoPackage
import com.novel.read.model.protocol.HotSearchResp
import com.novel.read.model.protocol.MarkResp
import com.novel.read.model.protocol.RankByUpdateResp
import com.novel.read.model.protocol.RecommendBookResp
import com.novel.read.model.protocol.RecommendListResp
import com.novel.read.model.protocol.SearchResp
import com.novel.read.model.protocol.UidResp
import com.novel.read.model.protocol.VersionResp

import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Created by Administrator on 2017/2/14.
 */

interface AccountService {

    //获取推荐书籍
    @GET
    fun getRecommendBook(@Url url: String): Call<RecommendBookResp>

    //获取书籍详情
    @GET
    fun getBookDetail(@Url url: String): Call<BookDetailResp>

    //获取书籍详情
    @GET
    fun getBookDetails(@Url url: String): Single<BookDetailResp>

    //小说类型
    @GET
    fun getCategoryType(@Url url: String): Call<CategoryTypeResp>

    //热搜
    @GET
    fun getHotSearch(@Url url: String): Call<HotSearchResp>

    //书籍章节
    @GET
    fun getBookArticle(@Url url: String): Call<BookArticleResp>

    //搜索
    @GET
    fun getSearchList(@Url url: String): Call<SearchResp>

    //获取推荐
    @GET
    fun getRecommendList(@Url url: String): Call<RecommendListResp>

    //获取最新排行
    @GET
    fun getRankByUpdate(@Url url: String): Call<RankByUpdateResp>

    //获取排行
    @GET
    fun getRankList(@Url url: String): Call<RankByUpdateResp>

    //获取新版本
    @GET
    fun checkVersion(@Url url: String): Call<VersionResp>

    //添加标签
    @POST
    fun addSign(@Url url: String, @Body body: RequestBody): Call<ErrorResponse>

    //删除标签
    @POST
    fun deleteSign(@Url url: String, @Body body: RequestBody): Call<ErrorResponse>

    //获取标签
    @GET
    fun getSignList(@Url url: String): Call<MarkResp>

    //登录
    @POST
    fun login(@Url url: String, @Body body: RequestBody): Call<UidResp>

    //获取书籍章节详情
    @GET
    fun getBookArticleDetail(@Url url: String): Single<ChapterInfoPackage>
}