package com.novel.read.ui.channel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.novel.read.base.BaseViewModel
import com.novel.read.constant.AppConst
import com.novel.read.constant.IntentAction
import com.novel.read.data.model.BookListResp
import com.novel.read.network.repository.BookRepository

class ChannelInfoViewModel(application: Application) : BaseViewModel(application) {

    private val bookRepository by lazy { BookRepository() }
    var bookListResp = MutableLiveData<List<BookListResp>>()
    private lateinit var mList: MutableList<BookListResp>
    val pageStatus = MutableLiveData<Int>()
    var page: Int = 1
    private var pageSize: Int = 20
    private var channelId: Long = 0L
    private var total: Int = 0

    fun initData(intent: Intent) {
        channelId = intent.getLongExtra(IntentAction.bookTypeId, 0L)
        launch(
            block = {
                pageStatus.value = AppConst.loading
                val data = bookRepository.getBookList(channelId, page, pageSize)
                total = data.count
                mList = data.bookList
                bookListResp.value = mList
                pageStatus.value = AppConst.complete
            },
            error = {
                pageStatus.value = AppConst.error
            }
        )
    }

    fun loadMore() {
        Log.e("loadMore", "loadMore: $page")
        launch(block = {
            pageStatus.value = AppConst.loadMore
            page++
            val mData = bookRepository.getBookList(channelId, page, pageSize).bookList
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