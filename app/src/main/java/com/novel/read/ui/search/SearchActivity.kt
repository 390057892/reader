package com.novel.read.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
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
import com.novel.read.lib.ATH
import com.novel.read.lib.dialogs.alert
import com.novel.read.lib.dialogs.noButton
import com.novel.read.lib.dialogs.yesButton
import com.novel.read.utils.ext.applyTint
import com.novel.read.utils.ext.getViewModel
import com.novel.read.utils.ext.requestInputMethod
import kotlinx.android.synthetic.main.activity_daily.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.view_search.*

//这个页面为了赶进度写的很蠢
class SearchActivity : VMBaseActivity<SearchViewModel>(R.layout.activity_search) {

    private lateinit var mHisAdapter: HistoryAdapter
    private lateinit var mHotAdapter: HotAdapter
    private lateinit var mSearchAdapter: SearchAdapter

    override val viewModel: SearchViewModel
        get() = getViewModel(SearchViewModel::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        ATH.applyEdgeEffectColor(rlv_history)
        ATH.applyEdgeEffectColor(rlv_hot)
        ATH.applyEdgeEffectColor(rlv_history)
        viewModel.initData()
        initHotAdapter()
        initHisAdapter()
        initSearchAdapter()
        initRecycleData()
        initClick()
        initLoadMore()
    }

    private fun initClick() {
        tv_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' } == "") {
                    head_hot.visibility = View.VISIBLE
                    head_history.visibility = View.VISIBLE
                    rlv_hot.visibility = View.VISIBLE
                    rlv_history.visibility = View.VISIBLE
                    rlv_search.visibility=View.GONE
                } else {
                    head_hot.visibility = View.GONE
                    head_history.visibility = View.GONE
                    rlv_hot.visibility = View.GONE
                    rlv_history.visibility = View.GONE
                    viewModel.page = 1
                    viewModel.searchKey = s.toString().trim()
                    onRefresh()
                    rlv_search.visibility=View.VISIBLE
                }

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        //键盘的搜索
        tv_search.setOnKeyListener { v, keyCode, event ->
            //修改回车键功能
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                saveKey()
                return@setOnKeyListener true
            }
            false
        }

        tv_cancel.setOnClickListener {
            onBackPressed()
        }

        head_history.setOnClickListener {
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
        rlv_search.layoutManager = LinearLayoutManager(this)
        mSearchAdapter = SearchAdapter()
        rlv_search.adapter = mSearchAdapter

        mSearchAdapter.setOnItemChildClickListener { adapter, view, position ->
            tv_search.setText(viewModel.mList[position].getBBookName())
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
        rlv_history.layoutManager = manager2
        rlv_history.adapter = mHisAdapter

        mHisAdapter.setOnItemClickListener { adapter, view, position ->
            rlv_search.visibility = View.VISIBLE
            tv_search.setText(viewModel.hisKey.value?.get(position)?.getBKey())
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
        rlv_hot.layoutManager = manager
        mHotAdapter = HotAdapter()
        rlv_hot.adapter = mHotAdapter
        mHotAdapter.setOnItemClickListener { adapter, view, position ->
            rlv_search.visibility = View.VISIBLE
            tv_search.setText(mHotAdapter.data[position])
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
        val key = tv_search.text.toString().trim { it <= ' ' }
        viewModel.saveKey(key)
    }

    private fun onRefresh() {
        mSearchAdapter.setEmptyView(R.layout.view_loading)
        viewModel.searchKey = tv_search.text.toString().trim()
        viewModel.searchBook()
    }

    private fun getErrorView(): View {
        val errorView: View = layoutInflater.inflate(R.layout.view_net_error, rlv_daily, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.message_fade_in, R.anim.message_fade_out)
    }
}