package com.novel.read.ui.rank

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.novel.read.base.BaseViewModel
import com.novel.read.constant.AppConst
import com.novel.read.data.model.BookListResp
import com.novel.read.network.repository.BookRepository

class RankViewModel(application: Application) : BaseViewModel(application) {

    private val bookRepository by lazy { BookRepository() }
    var bookListResp = MutableLiveData<List<BookListResp>>()
    private lateinit var mList: MutableList<BookListResp>
    val pageStatus = MutableLiveData<Int>()
    var page: Int = 1
    private var pageSize: Int = 20
    var rankType: Int = 1
    var pageType: Int = 1

    fun initData() {
        launch(block = {
            pageStatus.value = AppConst.loading
            Log.e("initData", "initData: $rankType", )
            val data = bookRepository.getBooRank(page, pageSize, rankType)
            mList = data.bookRankList
            bookListResp.value = mList
            pageStatus.value = AppConst.complete
        },error =  {
            pageStatus.value = AppConst.error
        })
    }

    fun loadMore() {
        Log.e("loadMore", "loadMore: $page")
        launch(block = {
            pageStatus.value = AppConst.loadMore
            page++
            val mData = bookRepository.getBooRank(page, pageSize, rankType).bookRankList
            mList.addAll(mData)
            bookListResp.value = mList
            pageStatus.value = AppConst.loadComplete
            if (pageSize > mData.size) {
                pageStatus.value = AppConst.noMore
            }
        }, error = {
            page--
            pageStatus.value = AppConst.loadMoreFail
        })
    }
}