package com.novel.read.ui.daily

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.AppConst
import com.novel.read.databinding.ActivityDailyBinding
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.getViewModel

class DailyActivity : VMBaseActivity<ActivityDailyBinding,DailyViewModel>() {
    
    override val viewModel: DailyViewModel
        get() = getViewModel(DailyViewModel::class.java)

    override fun getViewBinding(): ActivityDailyBinding {
        return ActivityDailyBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: DailyAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        ATH.applyEdgeEffectColor(binding.rlvDaily)
        initRecyclerView()
        upRecyclerData()
        initLoadMore()
    }

    private fun initRecyclerView() {
        binding.rlvDaily.layoutManager = LinearLayoutManager(this)
        adapter = DailyAdapter()
        binding.rlvDaily.adapter = adapter
    }

    private fun upRecyclerData() {
        onRefresh()
        viewModel.run {
            bookListResp.observe(this@DailyActivity) {
                adapter.setList(it)
            }
            pageStatus.observe(this@DailyActivity) {
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
        viewModel.initData()
    }

    private fun getErrorView(): View {
        val errorView: View = layoutInflater.inflate(R.layout.view_net_error, binding.rlvDaily, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }

}