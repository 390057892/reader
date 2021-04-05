package com.novel.read.ui.daily

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.novel.read.base.BaseViewModel
import com.novel.read.constant.AppConst
import com.novel.read.constant.LayoutType
import com.novel.read.data.model.BookListResp
import com.novel.read.network.repository.BookRepository

class DailyViewModel(application: Application) : BaseViewModel(application) {

    private val bookRepository by lazy { BookRepository() }
    var bookListResp = MutableLiveData<List<BookListResp>>()
    val pageStatus = MutableLiveData<Int>()
    private lateinit var mList: MutableList<BookListResp>
    var page: Int = 1
    private var pageSize: Int = 20

    fun initData() {
        launch(block = {
            pageStatus.value = AppConst.loading
            val data = bookRepository.getBooRank(page, pageSize, LayoutType.RECOMMEND)
            mList = data.bookRankList
            bookListResp.value = mList
            pageStatus.value = AppConst.complete
        }, error = {
            pageStatus.value = AppConst.error
        })
    }

    fun loadMore() {
        Log.e("loadMore", "loadMore: $page")
        launch(block = {
            pageStatus.value = AppConst.loadMore
            page++
//            val mData = bookRepository.getDailyList(page, pageSize).bookList
            val mData = bookRepository.getBooRank(page, pageSize, LayoutType.RECOMMEND).bookRankList
            mList.addAll(mData)
            bookListResp.value = mList
            pageStatus.value = AppConst.loadComplete
            if (pageSize > mData.size) {
                pageStatus.value = AppConst.noMore
            }
        },error =  {
            page--
            pageStatus.value = AppConst.loadMoreFail
        })
    }
}