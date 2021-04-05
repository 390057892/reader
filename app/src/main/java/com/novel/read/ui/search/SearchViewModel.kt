package com.novel.read.ui.search

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.novel.read.App
import com.novel.read.base.BaseViewModel
import com.novel.read.constant.AppConst
import com.novel.read.data.db.entity.SearchHistory
import com.novel.read.data.model.Pagination
import com.novel.read.data.model.SearchResp
import com.novel.read.network.repository.SearchRepository

class SearchViewModel(application: Application) : BaseViewModel(application) {

    private val searchRepository by lazy { SearchRepository() }
    var searchList = MutableLiveData<List<SearchResp>>()
    var hotKey = MutableLiveData<Pagination<String>>()
    var hisKey = MutableLiveData<List<SearchHistory>>()
    val pageStatus = MutableLiveData<Int>()
    lateinit var mList: MutableList<SearchResp>
    var searchKey: String = ""
    var page: Int = 1
    var pageSize: Int = 20

    fun initData() {
        getHisKey()
        getHotKey()
    }

    fun searchBook() {
        launch(block = {
            pageStatus.value = AppConst.loading
            val data = searchRepository.searchBook(page, pageSize, searchKey)
            mList = data.bookList
            searchList.value = mList
            if (pageSize > mList.size) {
                pageStatus.value = AppConst.noMore
            }
            pageStatus.value = AppConst.complete
        }, error = {
            pageStatus.value = AppConst.error
        })
    }

    fun loadMore() {
        launch(block = {
            pageStatus.value = AppConst.loadMore
            page++
            val mData = searchRepository.searchBook(page, pageSize, searchKey).bookList
            mList.addAll(mData)
            searchList.value = mList
            pageStatus.value = AppConst.loadComplete
            if (pageSize > mData.size) {
                pageStatus.value = AppConst.noMore
            }
        }, error = {
            page--
            pageStatus.value = AppConst.loadMoreFail
        })
    }

    private fun getHotKey() {
        launch({
            hotKey.value = searchRepository.hotKey()
        }, {
        })
    }

    fun getHisKey() {
        hisKey.postValue(App.db.getSearchDao().getListByTime())
    }

    fun saveKey(key: String) {
        val searchListTable = SearchHistory()
        searchListTable.key = key
        searchListTable.saveTime = System.currentTimeMillis()
        App.db.getSearchDao().insert(searchListTable, key)
        hisKey.postValue(App.db.getSearchDao().getListByTime())
    }
}