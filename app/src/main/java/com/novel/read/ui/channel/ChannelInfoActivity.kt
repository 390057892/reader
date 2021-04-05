package com.novel.read.ui.channel

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.AppConst
import com.novel.read.constant.IntentAction
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.getViewModel
import kotlinx.android.synthetic.main.activity_channel_info.*

class ChannelInfoActivity : VMBaseActivity<ChannelInfoViewModel>(R.layout.activity_channel_info) {
    override val viewModel: ChannelInfoViewModel
        get() = getViewModel(ChannelInfoViewModel::class.java)
    private lateinit var adapter: ChannelInfoAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        ATH.applyEdgeEffectColor(rlv_book)
        initRecyclerView()
        viewModel.initData(intent)
        upRecyclerData()
        initLoadMore()
    }

    private fun initRecyclerView() {
        rlv_book.layoutManager = LinearLayoutManager(this)
        adapter = ChannelInfoAdapter()
        rlv_book.adapter = adapter
    }

    private fun upRecyclerData() {
        val channelName = intent.getStringExtra(IntentAction.channelName) ?: ""
        title_bar.title = channelName
        onRefresh()
        viewModel.run {
            bookListResp.observe(this@ChannelInfoActivity) {
                adapter.setList(it)
            }
            pageStatus.observe(this@ChannelInfoActivity) {
                when (it) {
                    AppConst.loading -> {
                        adapter.isUseEmpty = true
                    }
                    AppConst.complete -> {
                        adapter.isUseEmpty = false
                    }
                    AppConst.loadMore -> {
                        adapter.loadMoreModule.isEnableLoadMore = true
                    }
                    AppConst.loadComplete -> {
                        adapter.loadMoreModule.loadMoreComplete()
                    }
                    AppConst.noMore -> {
                        adapter.loadMoreModule.loadMoreEnd()
                    }
                    AppConst.loadMoreFail -> {
                        adapter.loadMoreModule.loadMoreFail()
                    }
                    else -> {
                        adapter.setList(null)
                        adapter.setEmptyView(getErrorView())
                        adapter.isUseEmpty = true
                    }
                }
            }
        }
    }

    private fun initLoadMore() {
        adapter.loadMoreModule.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (viewModel.pageStatus.value == AppConst.loadMore) {
                    return
                }
                viewModel.loadMore()
            }
        })
        adapter.loadMoreModule.isAutoLoadMore = true
        adapter.loadMoreModule.isEnableLoadMoreIfNotFullPage=false
    }

    private fun onRefresh() {
        adapter.setEmptyView(R.layout.view_loading)
    }

    private fun getErrorView(): View {
        val errorView: View = layoutInflater.inflate(R.layout.view_net_error, rlv_book, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }

}