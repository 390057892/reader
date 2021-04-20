package com.novel.read.ui.rank

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.AppConst
import com.novel.read.constant.IntentAction
import com.novel.read.constant.LayoutType
import com.novel.read.data.model.*
import com.novel.read.databinding.ActivityRankBinding
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.getViewModel

class RankActivity : VMBaseActivity<ActivityRankBinding,RankViewModel>() {

    override val viewModel: RankViewModel
        get() = getViewModel(RankViewModel::class.java)

    override fun getViewBinding(): ActivityRankBinding {
        return ActivityRankBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: RankAdapter
    private lateinit var typeAdapter: TypeAdapter
    private lateinit var mTypes: List<TypeName>
    override fun onActivityCreated(savedInstanceState: Bundle?) = with(binding){
        ATH.applyEdgeEffectColor(rlvRank)
        ATH.applyEdgeEffectColor(rlvType)
        initRecyclerView()
        upRecyclerData()
        initLoadMore()
    }

    private fun initRecyclerView() {
        binding.rlvRank.layoutManager = LinearLayoutManager(this)
        adapter = RankAdapter()
        binding.rlvRank.adapter = adapter
        binding.rlvType.layoutManager = LinearLayoutManager(this)
        typeAdapter = TypeAdapter()
        binding.rlvType.adapter = typeAdapter

    }

    private fun upRecyclerData() {
        viewModel.pageType = intent.getIntExtra(IntentAction.homeType, AppConst.home)
        viewModel.rankType = intent.getIntExtra(IntentAction.rankType, LayoutType.HOT)

        when (viewModel.pageType) {
            AppConst.home -> {
                mTypes = rankList(viewModel.rankType)
            }
            AppConst.man -> {
                mTypes = manList(viewModel.rankType)
            }
            AppConst.woman -> {
                mTypes = womanList(viewModel.rankType)
            }
        }

        typeAdapter.setList(mTypes)
        typeAdapter.setOnItemClickListener { adapter, view, position ->
            for (i in 0 until typeAdapter.data.size) {
                typeAdapter.data[i].check = false
            }
            typeAdapter.data[position].check = true
            typeAdapter.notifyDataSetChanged()
            viewModel.rankType = typeAdapter.data[position].rankType
            viewModel.page = 1
            onRefresh()
        }

        onRefresh()
        viewModel.run {
            bookListResp.observe(this@RankActivity) {
                adapter.setList(it)
            }
            pageStatus.observe(this@RankActivity) {
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
        adapter.setList(null)
        adapter.setEmptyView(R.layout.view_loading)
        viewModel.initData()
    }

    private fun getErrorView(): View {
        val errorView: View = layoutInflater.inflate(R.layout.view_net_error, binding.rlvRank, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }



}