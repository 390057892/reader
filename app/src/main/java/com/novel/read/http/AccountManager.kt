package com.novel.read.http

import android.content.Context
import android.text.TextUtils
import android.util.Log

import com.mango.mangolib.event.EventManager
import com.mango.mangolib.http.MyRequestType
import com.mango.mangolib.http.ServiceCallback
import com.novel.read.event.AddBookSignEvent
import com.novel.read.event.BookArticleEvent
import com.novel.read.event.DeleteBookSignEvent
import com.novel.read.event.ErrorChapterEvent
import com.novel.read.event.FinishChapterEvent
import com.novel.read.event.GetBookDetailEvent
import com.novel.read.event.GetBookSignEvent
import com.novel.read.event.GetCategoryTypeEvent
import com.novel.read.event.GetRecommendBookEvent
import com.novel.read.event.HotSearchEvent
import com.novel.read.event.LoginEvent
import com.novel.read.event.SearchListEvent
import com.novel.read.event.VersionEvent
import com.novel.read.http.service.AccountService
import com.novel.read.model.db.ChapterInfoBean
import com.novel.read.model.db.dbManage.BookRepository
import com.novel.read.model.protocol.BookDetailResp
import com.novel.read.model.protocol.RankByUpdateResp
import com.novel.read.model.protocol.RecommendListResp
import com.novel.read.utlis.PhoneUtils
import com.novel.read.widget.page.TxtChapter

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

import java.util.ArrayDeque
import java.util.ArrayList
import java.util.HashMap

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Callback

/**
 * Created by zlj on 2017/2/14.
 */

class AccountManager private constructor() {

    private val accountService: AccountService =
        ServiceGenerator.createService(AccountService::class.java, MyRequestType.URL_TEXT)

    private var mChapterSub: Subscription? = null

    fun getRecommendBook(bookId: String, limit: String) {
        val map = HashMap<String, String>()
        map["book_id"] = bookId
        val call = accountService.getRecommendBook(getUrlString(Urls.getRecommend, map))
        call.enqueue(ServiceCallback(GetRecommendBookEvent::class.java))
    }

    fun getBookDetail(bookId: String) {
        val map = HashMap<String, String>()
        map["book_id"] = bookId
        val call = accountService.getBookDetail(getUrlString(Urls.getBookDetail, map))
        call.enqueue(ServiceCallback(GetBookDetailEvent::class.java))
    }

    fun getBookDetails(bookId: String): Single<BookDetailResp> { //rxjava 获取多书籍详情 合并请求调用
        val map = HashMap<String, String>()
        map["book_id"] = bookId
        return accountService.getBookDetails(getUrlString(Urls.getBookDetail, map))
    }

    fun getCategoryType() {
        val map = HashMap<String, String>()
        val call = accountService.getCategoryType(getUrlString(Urls.getCategoryType, map))
        call.enqueue(ServiceCallback(GetCategoryTypeEvent::class.java))
    }

    fun getHotSearch() {
        val map = HashMap<String, String>()
        val call = accountService.getHotSearch(getUrlString(Urls.getHotSearch, map))
        call.enqueue(ServiceCallback(HotSearchEvent::class.java))
    }

    fun getBookArticle(bookId: String, hasContent: String, page: String, limit: String) {
        val map = HashMap<String, String>()
        map["book_id"] = bookId
        map["has_content"] = hasContent
        map["page"] = page
        map["limit"] = limit
        val call = accountService.getBookArticle(getUrlString(Urls.getBookArticle, map))
        call.enqueue(ServiceCallback(BookArticleEvent::class.java))
    }


    fun getSearchBookList(category_id: String, key: String, page: Int) {
        val map = HashMap<String, String>()
        if (!TextUtils.isEmpty(category_id)) {
            map["category_id"] = category_id
        } else {
            map["category_id"] = "0"
        }
        if (!TextUtils.isEmpty(key)) {
            map["key"] = key
        }
        map["page"] = page.toString()
        val call = accountService.getSearchList(getUrlString(Urls.getBookList, map))
        call.enqueue(ServiceCallback(SearchListEvent::class.java))
    }

    fun getRecommendList(listType: String, callback: Callback<RecommendListResp>) {
        val map = HashMap<String, String>()
        map["type"] = listType
        val call = accountService.getRecommendList(getUrlString(Urls.getRecommendList, map))
        call.enqueue(callback)

    }

    fun getRankByUpdate(page: Int, limit: Int, callback: Callback<RankByUpdateResp>) {
        val map = HashMap<String, String>()
        if (!TextUtils.isEmpty(page.toString())) {
            map["page"] = page.toString()
        }
        if (limit != 0) {
            map["limit"] = limit.toString()
        }
        val call = accountService.getRankByUpdate(getUrlString(Urls.getRankByUpdate, map))
        call.enqueue(callback)
    }

    fun getRankList(
        type: String,
        sex: String,
        dateType: String,
        page: String,
        callback: Callback<RankByUpdateResp>
    ) {
        val map = HashMap<String, String>()
        map["type"] = type
        map["gender"] = sex
        map["date_type"] = dateType
        map["page"] = page
        val call = accountService.getRankList(getUrlString(Urls.getRankList, map))
        call.enqueue(callback)
    }

    fun checkVersion(versionCode: Int) {
        val map = HashMap<String, String>()
        map["version"] = versionCode.toString()
        //        map.put("shell", Constant.shell);
        val call = accountService.checkVersion(getUrlString(Urls.checkVersion, map))
        call.enqueue(ServiceCallback(VersionEvent::class.java))
    }

    fun addSign(bookId: String, articleId: String, content: String) {
        val map = HashMap<String, String>()
        map["book_id"] = bookId
        map["article_id"] = articleId
        map["content"] = content
        val call = accountService.addSign(Urls.addBookSign, mapToBody(map))
        call.enqueue(ServiceCallback(AddBookSignEvent::class.java))
    }

    fun deleteSign(signIds: String) {
        val map = HashMap<String, String>()
        map["sign_ids"] = signIds
        val call = accountService.deleteSign(Urls.deleteSign, mapToBody(map))
        call.enqueue(ServiceCallback(DeleteBookSignEvent::class.java))
    }

    fun getSignList(bookId: String) {
        val map = HashMap<String, String>()
        map["book_id"] = bookId
        val call = accountService.getSignList(getUrlString(Urls.getBookSign, map))
        call.enqueue(ServiceCallback(GetBookSignEvent::class.java))
    }

    fun login(mContext: Context) {
        val map = HashMap<String, String>()
        Log.e("getUniquePsuedoID", "login: " + PhoneUtils.uniquePsuedoID)
        map["code"] = PhoneUtils.uniquePsuedoID
        val call = accountService.login(Urls.login, mapToBody(map))
        call.enqueue(ServiceCallback(LoginEvent::class.java))
    }

    fun getBookArticleDetail(bookId: String?, bookChapters: List<TxtChapter>) {
        val size = bookChapters.size
        //取消上次的任务，防止多次加载
        if (mChapterSub != null) {
            mChapterSub!!.cancel()
        }
        val chapterInfos = ArrayList<Single<ChapterInfoBean>>(bookChapters.size)
        val titles = ArrayDeque<String>(bookChapters.size)

        // 将要下载章节，转换成网络请求。
        for (i in 0 until size) {
            val bookChapter = bookChapters[i]
            // 网络中获取数据
            val chapterInfoSingle = getChapterInfo(bookChapter.chapterId)
            chapterInfos.add(chapterInfoSingle)
            titles.add(bookChapter.title)
        }

        Single.concat(chapterInfos)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<ChapterInfoBean> {
                internal var title = titles.poll()

                override fun onSubscribe(s: Subscription) {
                    s.request(Integer.MAX_VALUE.toLong())
                    mChapterSub = s
                }

                override fun onNext(chapterInfoBean: ChapterInfoBean) {
                    //存储数据
                    BookRepository.getInstance().saveChapterInfo(
                        bookId, title, chapterInfoBean.body
                    )
                    EventManager.instance.postEvent(FinishChapterEvent())
                    //将获取到的数据进行存储
                    title = titles.poll()
                }

                override fun onError(t: Throwable) {
                    //只有第一个加载失败才会调用errorChapter
                    if (bookChapters[0].title == title) {
                        EventManager.instance.postEvent(ErrorChapterEvent())
                    }
                }

                override fun onComplete() {}
            }
            )
    }

    /**
     * 注意这里用的是同步请求
     */
    fun getChapterInfo(id: String): Single<ChapterInfoBean> {
        val map = HashMap<String, String>()
        map["article_id"] = id
        return accountService.getBookArticleDetail(getUrlString(Urls.getDetail, map))
            .map { bean -> bean.article?.get(0) }
    }

    /**
     * get方法拼接字符串
     */
    private fun getUrlString(path: String, query: HashMap<String, String>?): String {
        var mypath = path
        if (query != null && query.size > 0) {
            val pathWithQuery = StringBuilder(path)
            if (!path.contains("?")) {
                pathWithQuery.append("?")
            } else {
                pathWithQuery.append("&")
            }

            for (stringStringEntry in query.entries) {
                val key = (stringStringEntry as Map.Entry<*, *>).key as String
                val `val` = (stringStringEntry as Map.Entry<*, *>).value as String
                pathWithQuery.append(key)
                pathWithQuery.append("=")
                pathWithQuery.append(`val`)
                pathWithQuery.append("&")
            }

            pathWithQuery.deleteCharAt(pathWithQuery.length - 1)
            mypath = pathWithQuery.toString()
        }

        return mypath
    }

    /**
     * 减少请求info类的数量,直接用map替代实体类，返回body
     */
    private fun mapToBody(map: HashMap<String, String>): RequestBody {
        return RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            ServiceGenerator.formatResponse(map)
        )
    }

    companion object {

        private var instance: AccountManager? = null

        @Synchronized
        fun getInstance(): AccountManager {
            if (instance == null) {
                instance = AccountManager()
            }
            return instance as AccountManager
        }
    }

}
