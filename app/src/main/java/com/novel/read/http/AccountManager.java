package com.novel.read.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mango.mangolib.event.EventManager;
import com.mango.mangolib.http.ErrorResponse;
import com.mango.mangolib.http.MyRequestType;
import com.mango.mangolib.http.ServiceCallback;
import com.novel.read.constants.Constant;
import com.novel.read.event.AddBookSignEvent;
import com.novel.read.event.BookArticleEvent;
import com.novel.read.event.DeleteBookSignEvent;
import com.novel.read.event.ErrorChapterEvent;
import com.novel.read.event.FinishChapterEvent;
import com.novel.read.event.GetBookDetailEvent;
import com.novel.read.event.GetBookSignEvent;
import com.novel.read.event.GetCategoryTypeEvent;
import com.novel.read.event.GetRecommendBookEvent;
import com.novel.read.event.HotSearchEvent;
import com.novel.read.event.LoginEvent;
import com.novel.read.event.SearchListEvent;
import com.novel.read.event.VersionEvent;
import com.novel.read.http.service.AccountService;
import com.novel.read.model.db.ChapterInfoBean;
import com.novel.read.model.db.dbManage.BookRepository;
import com.novel.read.model.protocol.BookArticleResp;
import com.novel.read.model.protocol.BookDetailResp;
import com.novel.read.model.protocol.CategoryTypeResp;
import com.novel.read.model.protocol.HotSearchResp;
import com.novel.read.model.protocol.MarkResp;
import com.novel.read.model.protocol.RankByUpadateResp;
import com.novel.read.model.protocol.RecommendBookResp;
import com.novel.read.model.protocol.RecommendListResp;
import com.novel.read.model.protocol.SearchResp;
import com.novel.read.model.protocol.UidResp;
import com.novel.read.model.protocol.VersionResp;
import com.novel.read.utlis.LogUtils;
import com.novel.read.utlis.PhoneUtils;
import com.novel.read.widget.page.TxtChapter;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by JillFung on 2017/2/14.
 */

public class AccountManager {

    private AccountService accountService;

    private AccountManager() {
        accountService = ServiceGenerator.createService(AccountService.class, MyRequestType.URL_TEXT);
    }

    private static AccountManager instance = null;

    public static synchronized AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }


    public void getRecommendBook(String bookId,String limit) {
        HashMap<String, String> map = new HashMap<>();
        map.put("book_id", bookId);
        Call<RecommendBookResp> call = accountService.getRecommendBook(getUrlString(Urls.getRecommend,map));
        call.enqueue(new ServiceCallback<>(GetRecommendBookEvent.class));
    }

    public void getBookDetail(String bookId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("book_id", bookId);
        Call<BookDetailResp> call = accountService.getBookDetail(getUrlString(Urls.getBookDetail,map));
        call.enqueue(new ServiceCallback<>(GetBookDetailEvent.class));
    }

    public Single<BookDetailResp> getBookDetails(String bookId){ //rxjava 获取多书籍详情 合并请求调用
        HashMap<String, String> map = new HashMap<>();
        map.put("book_id", bookId);
        return accountService.getBookDetails(getUrlString(Urls.getBookDetail,map));
    }

    public void getCategoryType() {
        HashMap<String, String> map = new HashMap<>();
        Call<CategoryTypeResp> call = accountService.getCategoryType(getUrlString(Urls.getCategoryType,map));
        call.enqueue(new ServiceCallback<>(GetCategoryTypeEvent.class));
    }

    public void getHotSearch() {
        HashMap<String, String> map = new HashMap<>();
        Call<HotSearchResp> call = accountService.getHotSearch(getUrlString(Urls.getHotSearch,map));
        call.enqueue(new ServiceCallback<>(HotSearchEvent.class));
    }


    public void getBookArticle(String bookId,String hasContent,String page,String limit) {
        HashMap<String, String> map = new HashMap<>();
        map.put("book_id", bookId);
        map.put("has_content", hasContent);
        map.put("page", page);
        map.put("limit", limit);
        Call<BookArticleResp> call = accountService.getBookArticle(getUrlString(Urls.getBookArticle,map));
        call.enqueue(new ServiceCallback<>(BookArticleEvent.class));
    }


    public void getSearchBookList(String category_id,String key,int page) {
        HashMap<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(category_id)) {
            map.put("category_id", category_id);
        }else {
            map.put("category_id", "0");
        }
        if (!TextUtils.isEmpty(key)){
            map.put("key", key);
        }
        map.put("page", String.valueOf(page));
        Call<SearchResp> call = accountService.getSearchList(getUrlString(Urls.getBookList,map));
        call.enqueue(new ServiceCallback<>(SearchListEvent.class));
    }

    public void getRecommendList(String listType, Callback<RecommendListResp> callback) {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", listType);
        Call<RecommendListResp> call = accountService.getRecommendList(getUrlString(Urls.getRecommendList,map));
        call.enqueue(callback);

    }

    public void getRankByUpdate(int page, int limit, Callback<RankByUpadateResp> callback) {
        HashMap<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(String.valueOf(page))) {
            map.put("page", String.valueOf(page));
        }
        if (limit!=0){
            map.put("limit", String.valueOf(limit));
        }
        Call<RankByUpadateResp> call = accountService.getRankByUpdate(getUrlString(Urls.getRankByUpdate,map));
        call.enqueue(callback);
    }

    public void getRankList(String type, String sex, String dateType, String page, Callback<RankByUpadateResp> callback){
        HashMap<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("gender", sex);
        map.put("date_type", dateType);
        map.put("page", page);
        Call<RankByUpadateResp> call = accountService.getRankList(getUrlString(Urls.getRankList, map));
        call.enqueue(callback);
    }

    public void checkVersion(int versionCode){
        HashMap<String, String> map = new HashMap<>();
        map.put("version", String.valueOf(versionCode));
//        map.put("shell", Constant.shell);
        Call<VersionResp> call = accountService.checkVersion(getUrlString(Urls.checkVersion, map));
        call.enqueue(new ServiceCallback<>(VersionEvent.class));
    }

    public void addSign(String bookId,String articleId,String content){
        HashMap<String, String> map = new HashMap<>();
        map.put("book_id", bookId);
        map.put("article_id", articleId);
        map.put("content", content);
        Call<ErrorResponse> call = accountService.addSign(Urls.addBookSign, mapToBody(map));
        call.enqueue(new ServiceCallback<>(AddBookSignEvent.class));
    }

    public void deleteSign(String signIds){
        HashMap<String, String> map = new HashMap<>();
        map.put("sign_ids", signIds);
        Call<ErrorResponse> call = accountService.deleteSign(Urls.deleteSign, mapToBody(map));
        call.enqueue(new ServiceCallback<>(DeleteBookSignEvent.class));
    }

    public void getSignList(String bookId){
        HashMap<String, String> map = new HashMap<>();
        map.put("book_id", bookId);
        Call<MarkResp> call = accountService.getSignList(getUrlString(Urls.getBookSign, map));
        call.enqueue(new ServiceCallback<>(GetBookSignEvent.class));
    }

    public void login(Context mContext){
        HashMap<String, String> map = new HashMap<>();
        Log.e("getUniquePsuedoID", "login: "+ PhoneUtils.getUniquePsuedoID());
        map.put("code", PhoneUtils.getUniquePsuedoID());
        Call<UidResp> call = accountService.login(Urls.login, mapToBody(map));
        call.enqueue(new ServiceCallback<>(LoginEvent.class));
    }

    private Subscription mChapterSub;

    public void getBookArticleDetail(String bookId,List<TxtChapter> bookChapters){
        int size = bookChapters.size();
        //取消上次的任务，防止多次加载
        if (mChapterSub != null) {
            mChapterSub.cancel();
        }
        List<Single<ChapterInfoBean>> chapterInfos = new ArrayList<>(bookChapters.size());
        ArrayDeque<String> titles = new ArrayDeque<>(bookChapters.size());

        // 将要下载章节，转换成网络请求。
        for (int i = 0; i < size; ++i) {
            TxtChapter bookChapter = bookChapters.get(i);
            // 网络中获取数据
            Single<ChapterInfoBean> chapterInfoSingle = getChapterInfo(bookChapter.getChapterId());
            chapterInfos.add(chapterInfoSingle);
            titles.add(bookChapter.getTitle());
        }

        Single.concat(chapterInfos)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChapterInfoBean>() {
                            String title = titles.poll();

                            @Override
                            public void onSubscribe(Subscription s) {
                                s.request(Integer.MAX_VALUE);
                                mChapterSub = s;
                            }

                            @Override
                            public void onNext(ChapterInfoBean chapterInfoBean) {
                                //存储数据
                                BookRepository.getInstance().saveChapterInfo(
                                        bookId, title, chapterInfoBean.getBody()
                                );
                                EventManager.Companion.getInstance().postEvent(new FinishChapterEvent());
                                //将获取到的数据进行存储
                                title = titles.poll();
                            }

                            @Override
                            public void onError(Throwable t) {
                                //只有第一个加载失败才会调用errorChapter
                                if (bookChapters.get(0).getTitle().equals(title)) {
                                    EventManager.Companion.getInstance().postEvent(new ErrorChapterEvent());
                                }
                                LogUtils.e(t);
                            }

                            @Override
                            public void onComplete() {
                            }
                        }
                );
    }

    /**
     * 注意这里用的是同步请求
     */
    public Single<ChapterInfoBean> getChapterInfo(String id){
        HashMap<String,String> map = new HashMap<>();
        map.put("article_id", id);
        return accountService.getBookArticleDetail(getUrlString(Urls.getDetail, map))
                .map(bean -> bean.getArticle().get(0));
    }

    /**
     * get方法拼接字符串
     */
    private String getUrlString(String path, HashMap<String, String> query) {
        String mypath = path;
        if (query != null && query.size() > 0) {
            StringBuilder pathWithQuery = new StringBuilder(path);
            if (!path.contains("?")) {
                pathWithQuery.append("?");
            } else {
                pathWithQuery.append("&");
            }

            for (Map.Entry<String, String> stringStringEntry : query.entrySet()) {
                String key = (String) ((Map.Entry) stringStringEntry).getKey();
                String val = (String) ((Map.Entry) stringStringEntry).getValue();
                pathWithQuery.append(key);
                pathWithQuery.append("=");
                pathWithQuery.append(val);
                pathWithQuery.append("&");
            }

            pathWithQuery.deleteCharAt(pathWithQuery.length() - 1);
            mypath = pathWithQuery.toString();
        }

        return mypath;
    }

    /**
     * 减少请求info类的数量,直接用map替代实体类，返回body
     */
    private RequestBody mapToBody(HashMap<String,String> map){
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), ServiceGenerator.formatResponse(map));
    }

}
