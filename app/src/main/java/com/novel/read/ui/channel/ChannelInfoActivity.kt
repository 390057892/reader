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
import com.novel.read.databinding.ActivityChannelInfoBinding
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.getViewModel

class ChannelInfoActivity : VMBaseActivity<ActivityChannelInfoBinding,ChannelInfoViewModel>() {
    
    override val viewModel: ChannelInfoViewModel
        get() = getViewModel(ChannelInfoViewModel::class.java)
    
    override fun getViewBinding(): ActivityChannelInfoBinding {
        return ActivityChannelInfoBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: ChannelInfoAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        ATH.applyEdgeEffectColor(binding.rlvBook)
        initRecyclerView()
        viewModel.initData(intent)
        upRecyclerData()
        initLoadMore()
    }

    private fun initRecyclerView() {
        binding.rlvBook.layoutManager = LinearLayoutManager(this)
        adapter = ChannelInfoAdapter()
        binding.rlvBook.adapter = adapter
    }

    private fun upRecyclerData() {
        val channelName = intent.getStringExtra(IntentAction.channelName) ?: ""
        binding.titleBar.title = channelName
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
        val errorView: View = layoutInflater.inflate(R.layout.view_net_error, binding.rlvBook, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }

  
}