package com.novel.read.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.adapter.BookListAdapter
import com.novel.read.base.NovelBaseActivity
import com.novel.read.constants.Constant
import com.novel.read.constants.Constant.COMMENT_SIZE
import com.novel.read.event.SearchListEvent
import com.novel.read.http.AccountManager
import com.novel.read.inter.OnLoadMoreListener
import com.novel.read.model.protocol.SearchResp
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_book_type_list.*
import java.util.*

class NovelBookTypeListActivity : NovelBaseActivity() {

    private var mList: MutableList<SearchResp.BookBean> = ArrayList()
    private var mAdapter: BookListAdapter? = null
    private var mCategoryId: String? = null
    private var mTitle: String? = null
    private var page = 1
    private var loadSize: Int = 0

    override val layoutId: Int get() = R.layout.activity_book_type_list

    override fun initView() {
        EventManager.instance.registerSubscriber(this)

        mCategoryId = intent.getStringExtra(Constant.Bundle.CategoryId)
        mTitle = intent.getStringExtra(Constant.Bundle.mTitle)

        rlv_type_list.layoutManager = LinearLayoutManager(this)
        mAdapter = BookListAdapter(mList, rlv_type_list)
        rlv_type_list.adapter = mAdapter

        mAdapter!!.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (mAdapter!!.isLoadingMore) {

                } else {
                    if (loadSize >= COMMENT_SIZE) {
                        mAdapter!!.isLoadingMore = true
                        mList.add(SearchResp.BookBean())
                        mAdapter!!.notifyDataSetChanged()
                        page++
                        getData()
                    }
                }
            }
        })
    }

    override fun initData() {
        refresh.showLoading()
        refresh.setOnReloadingListener { this.getData() }
        getData()
        toolbar.title = mTitle
        toolbar.setNavigationOnClickListener { view -> finish() }

    }

    private fun getData() {
        AccountManager.getInstance().getSearchBookList(mCategoryId, "", page)
    }

    @Subscribe
    fun getSearchList(event: SearchListEvent) {
        refresh.showFinish()
        if (event.isFail) {
            refresh.showError()
        } else {
            loadSize = event.result!!.book.size
            if (mAdapter!!.isLoadingMore) {
                mList.removeAt(mList.size - 1)
                mList.addAll(event.result!!.book)
                mAdapter!!.notifyDataSetChanged()
                mAdapter!!.isLoadingMore = false
            } else {
                mList.clear()
                mList.addAll(event.result!!.book)
                mAdapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.instance.unregisterSubscriber(this)
    }
}
