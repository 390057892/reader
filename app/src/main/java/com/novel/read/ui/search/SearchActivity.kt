package com.novel.read.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.AppConst
import com.novel.read.databinding.ActivitySearchBinding
import com.novel.read.lib.ATH
import com.novel.read.lib.dialogs.alert
import com.novel.read.lib.dialogs.noButton
import com.novel.read.lib.dialogs.yesButton
import com.novel.read.utils.ext.*

//这个页面写的很蠢
class SearchActivity : VMBaseActivity<ActivitySearchBinding,SearchViewModel>() {

    private lateinit var mHisAdapter: HistoryAdapter
    private lateinit var mHotAdapter: HotAdapter
    private lateinit var mSearchAdapter: SearchAdapter

    override val viewModel: SearchViewModel
        get() = getViewModel(SearchViewModel::class.java)

    override fun getViewBinding(): ActivitySearchBinding {
        return ActivitySearchBinding.inflate(layoutInflater)
    }

    private lateinit var tvSearch: TextView
    private lateinit var tvCancel: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?)= with(binding) {
        ATH.applyEdgeEffectColor(binding.rlvHistory)
        ATH.applyEdgeEffectColor(binding.rlvHot)
        ATH.applyEdgeEffectColor(binding.rlvSearch)
        tvSearch=binding.titleBar.findViewById(R.id.tv_search)
        tvCancel=binding.titleBar.findViewById(R.id.tv_cancel)
        viewModel.initData()
        initHotAdapter()
        initHisAdapter()
        initSearchAdapter()
        initRecycleData()
        initClick()
        initLoadMore()
    }

    private fun initClick() = with(binding){
        tvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' } == "") {
                    headHot.visibility = View.VISIBLE
                    headHistory.visibility = View.VISIBLE
                    rlvHot.visibility = View.VISIBLE
                    rlvHistory.visibility = View.VISIBLE
                    rlvSearch.visibility=View.GONE
                } else {
                    headHot.visibility = View.GONE
                    headHistory.visibility = View.GONE
                    rlvHot.visibility = View.GONE
                    rlvHistory.visibility = View.GONE
                    viewModel.page = 1
                    viewModel.searchKey = s.toString().trim()
                    onRefresh()
                    rlvSearch.visibility=View.VISIBLE
                }

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        //键盘的搜索
        tvSearch.setOnKeyListener { v, keyCode, event ->
            //修改回车键功能
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                saveKey()
                return@setOnKeyListener true
            }
            false
        }

        tvCancel.setOnClickListener {
            onBackPressed()
        }

        binding.headHistory.setOnClickListener {
            alert(title = "操作提示", message = "確認清空全部記錄?") {
                yesButton {
                    App.db.getSearchDao().deleteAll()
                    viewModel.getHisKey()
                }
                noButton {

                }
            }.show().applyTint().requestInputMethod()
        }
    }

    private fun initRecycleData() {
        viewModel.run {
            hisKey.observe(this@SearchActivity) {
                mHisAdapter.setList(it)
            }
            hotKey.observe(this@SearchActivity) {
                if (it.searchTermsList.size > 8) {
                    mHotAdapter.setList(it.searchTermsList.subList(0, 7))
                } else {
                    mHotAdapter.setList(it.searchTermsList)
                }
            }
            searchList.observe(this@SearchActivity) {
                mSearchAdapter.setList(it)
            }
            pageStatus.observe(this@SearchActivity) {
                when (it) {
                    AppConst.loading -> {
                        mSearchAdapter.isUseEmpty = true
                    }
                    AppConst.complete -> {
                        mSearchAdapter.isUseEmpty = false
                    }
                    AppConst.loadMore -> {
                        mSearchAdapter.loadMoreModule.isEnableLoadMore = true
                    }
                    AppConst.loadComplete -> {
                        mSearchAdapter.loadMoreModule.loadMoreComplete()
                    }
                    AppConst.noMore -> {
                        mSearchAdapter.loadMoreModule.loadMoreEnd()
                    }
                    AppConst.loadMoreFail -> {
                        mSearchAdapter.loadMoreModule.loadMoreFail()
                    }
                    else -> {
                        mSearchAdapter.setList(null)
                        mSearchAdapter.setEmptyView(getErrorView())
                        mSearchAdapter.isUseEmpty = true
                    }
                }
            }
        }
    }

    private fun initSearchAdapter() {
        binding.rlvSearch.layoutManager = LinearLayoutManager(this)
        mSearchAdapter = SearchAdapter()
        binding.rlvSearch.adapter = mSearchAdapter

        mSearchAdapter.setOnItemChildClickListener { adapter, view, position ->
            tvSearch.text = viewModel.mList[position].getBBookName()
            saveKey()
        }
    }

    private fun initHisAdapter() {
        val manager2 = FlexboxLayoutManager(this)
        //设置主轴排列方式
        manager2.flexDirection = FlexDirection.ROW
        //设置是否换行
        manager2.flexWrap = FlexWrap.WRAP
        manager2.alignItems = AlignItems.STRETCH
        mHisAdapter = HistoryAdapter()
        binding.rlvHistory.layoutManager = manager2
        binding.rlvHistory.adapter = mHisAdapter

        mHisAdapter.setOnItemClickListener { adapter, view, position ->
            binding.rlvSearch.visibility = View.VISIBLE
            tvSearch.setText(viewModel.hisKey.value?.get(position)?.getBKey())
            saveKey()
        }
    }

    private fun initHotAdapter() {
        val manager = FlexboxLayoutManager(this)
        //设置主轴排列方式
        manager.flexDirection = FlexDirection.ROW
        //设置是否换行
        manager.flexWrap = FlexWrap.WRAP
        manager.alignItems = AlignItems.STRETCH
        binding.rlvHot.layoutManager = manager
        mHotAdapter = HotAdapter()
        binding.rlvHot.adapter = mHotAdapter
        mHotAdapter.setOnItemClickListener { adapter, view, position ->
            binding.rlvSearch.visibility = View.VISIBLE
            tvSearch.text = mHotAdapter.data[position]
            saveKey()
        }
    }

    private fun initLoadMore() {
        mSearchAdapter.loadMoreModule.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (viewModel.pageStatus.value == AppConst.loadMore) {
                    return
                }
                viewModel.loadMore()
            }
        })
        mSearchAdapter.loadMoreModule.isAutoLoadMore = true
        mSearchAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
    }

    private fun saveKey() {
        val key = tvSearch.text.toString().trim { it <= ' ' }
        viewModel.saveKey(key)
    }

    private fun onRefresh() {
        mSearchAdapter.setEmptyView(R.layout.view_loading)
        viewModel.searchKey = tvSearch.text.toString().trim()
        viewModel.searchBook()
    }

    private fun getErrorView(): View {
        val errorView: View = layoutInflater.inflate(R.layout.view_net_error, binding.rlvSearch, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.message_fade_in, R.anim.message_fade_out)
    }

}