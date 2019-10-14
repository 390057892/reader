package com.novel.read.http.service;

import com.mango.mangolib.http.ErrorResponse;
import com.novel.read.model.protocol.BookArticleResp;
import com.novel.read.model.protocol.BookDetailResp;
import com.novel.read.model.protocol.CategoryTypeResp;
import com.novel.read.model.protocol.ChapterInfoPackage;
import com.novel.read.model.protocol.HotSearchResp;
import com.novel.read.model.protocol.MarkResp;
import com.novel.read.model.protocol.RankByUpadateResp;
import com.novel.read.model.protocol.RecommendBookResp;
import com.novel.read.model.protocol.RecommendListResp;
import com.novel.read.model.protocol.SearchResp;
import com.novel.read.model.protocol.UidResp;
import com.novel.read.model.protocol.VersionResp;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/2/14.
 */

public interface AccountService {

    //获取推荐书籍
    @GET
    Call<RecommendBookResp> getRecommendBook(@Url String url);

    //获取书籍详情
    @GET
    Call<BookDetailResp> getBookDetail(@Url String url);

    //获取书籍详情
    @GET
    Single<BookDetailResp> getBookDetails(@Url String url);

    //小说类型
    @GET
    Call<CategoryTypeResp> getCategoryType(@Url String url);

    //热搜
    @GET
    Call<HotSearchResp> getHotSearch(@Url String url);

    //书籍章节
    @GET
    Call<BookArticleResp> getBookArticle(@Url String url);

    //搜索
    @GET
    Call<SearchResp> getSearchList(@Url String url);

    //获取推荐
    @GET
    Call<RecommendListResp> getRecommendList(@Url String url);

    //获取最新排行
    @GET
    Call<RankByUpadateResp> getRankByUpdate(@Url String url);

    //获取排行
    @GET
    Call<RankByUpadateResp> getRankList(@Url String url);

    //获取新版本
    @GET
    Call<VersionResp> checkVersion(@Url String url);

    //添加标签
    @POST
    Call<ErrorResponse> addSign(@Url String url, @Body RequestBody body);

    //删除标签
    @POST
    Call<ErrorResponse> deleteSign(@Url String url, @Body RequestBody body);

    //获取标签
    @GET
    Call<MarkResp> getSignList(@Url String url);

    //登录
    @POST
    Call<UidResp> login(@Url String url, @Body RequestBody body);

    //获取书籍章节详情
    @GET
    Single<ChapterInfoPackage> getBookArticleDetail(@Url String url);
}