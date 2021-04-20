package com.novel.read.ui.end

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.AppConst
import com.novel.read.databinding.ActivityEndBinding
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.getViewModel

class EndActivity : VMBaseActivity<ActivityEndBinding,EndViewModel>() {

    override val viewModel: EndViewModel
        get() = getViewModel(EndViewModel::class.java)

    override fun getViewBinding(): ActivityEndBinding {
        return ActivityEndBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: EndAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        ATH.applyEdgeEffectColor(binding.rlvEnd)
        initRecyclerView()
        upRecyclerData()
        initLoadMore()
    }

    private fun initRecyclerView() {
        binding.rlvEnd.layoutManager = LinearLayoutManager(this)
        adapter = EndAdapter()
        binding.rlvEnd.adapter = adapter
    }

    private fun upRecyclerData() {
        onRefresh()
        viewModel.run {
            bookListResp.observe(this@EndActivity) {
                adapter.setList(it)
            }
            pageStatus.observe(this@EndActivity) {
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
        adapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
    }

    private fun onRefresh() {
        adapter.setEmptyView(R.layout.view_loading)
        viewModel.initData()
    }

    private fun getErrorView(): View {
        val errorView: View = layoutInflater.inflate(R.layout.view_net_error, binding.rlvEnd, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }



}