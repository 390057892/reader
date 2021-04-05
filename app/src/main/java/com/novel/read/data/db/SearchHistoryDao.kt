package com.novel.read.data.db

import com.novel.read.data.db.entity.SearchHistory
import org.litepal.LitePal

class SearchHistoryDao {

    fun getListByTime(): MutableList<SearchHistory>? =
        LitePal.order("saveTime desc").limit(5).find(SearchHistory::class.java)

    fun insert(searchHistory: SearchHistory, key: String) {
        searchHistory.saveOrUpdate("key=?", key)
    }

    fun deleteAll() {
        LitePal.deleteAll(SearchHistory::class.java)
    }

}