package com.novel.read.ui.daily

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.AppConst
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.getViewModel
import kotlinx.android.synthetic.main.activity_daily.*

class DailyActivity : VMBaseActivity<DailyViewModel>(R.layout.activity_daily) {
    
    override val viewModel: DailyViewModel
        get() = getViewModel(DailyViewModel::class.java)

    private lateinit var adapter: DailyAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        ATH.applyEdgeEffectColor(rlv_daily)
        initRecyclerView()
        upRecyclerData()
        initLoadMore()
    }

    private fun initRecyclerView() {
        rlv_daily.layoutManager = LinearLayoutManager(this)
        adapter = DailyAdapter()
        rlv_daily.adapter = adapter
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
        val errorView: View = layoutInflater.inflate(R.layout.view_net_error, rlv_daily, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }

}