package com.novel.read.http

/**
 * create by 赵利君 on 2019/10/14
 * describe:
 */

object Urls {

    internal const val getRecommend = "api/book/getRecommendByBook/" //获取推荐书籍
    internal const val getBookDetail = "api/book/getBookDetail/" //获取书籍详情
    internal const val getCategoryType = "api/category/getCategoryList/" //获取小说分类
    internal const val getHotSearch = "api/search/getHotSearch/" //获取热搜墙
    internal const val getBookArticle = "api/book/getBookArticle/" //获取书籍章节

    internal const val getBookList = "api/book/getBookList/" //获取和搜索书籍列表
    internal const val getRecommendList = "api/rank/getRecommendList/" //获取推荐列表
    internal const val getRankByUpdate = "api/rank/getRankByUpdate/" //最新更新
    internal const val getRankList = "api/rank/getRankList/" //获取排行榜
    internal const val checkVersion = " api/version/checkVersion/" //检测新版本
    internal const val addBookSign = " api/sign/addBookSign/" //添加书签
    internal const val getBookSign = " api/sign/getBookSignList/" //获取书签
    internal const val deleteSign = " api/sign/deleteSign/" //获取书签

    internal const val login = "api/login/checkLogin"//登录

    internal const val getDetail = "api/book/getArticleDetail"//获取章节详情

}