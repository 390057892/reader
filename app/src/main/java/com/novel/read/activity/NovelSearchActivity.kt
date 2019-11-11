package com.novel.read.activity

import android.content.DialogInterface
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.adapter.HistoryAdapter
import com.novel.read.adapter.HotAdapter
import com.novel.read.adapter.SearchAdapter
import com.novel.read.base.NovelBaseActivity
import com.novel.read.constants.Constant.COMMENT_SIZE
import com.novel.read.event.HotSearchEvent
import com.novel.read.event.SearchListEvent
import com.novel.read.http.AccountManager
import com.novel.read.inter.OnLoadMoreListener
import com.novel.read.model.db.SearchListTable
import com.novel.read.model.protocol.SearchResp
import com.novel.read.utlis.DialogUtils
import com.spreada.utils.chinese.ZHConverter
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.title_search.*
import org.litepal.LitePal
import java.util.*

class NovelSearchActivity : NovelBaseActivity() {

    private val mHotList = ArrayList<String>()
    private var mHotAdapter: HotAdapter? = null

    private var mHisList: MutableList<SearchListTable> = ArrayList()
    private var mHisAdapter: HistoryAdapter? = null

    private val mSearchList = ArrayList<SearchResp.BookBean>()
    private var mSearchAdapter: SearchAdapter? = null

    private var page = 1
    private var loadSize: Int = 0

    override val layoutId: Int get() = R.layout.activity_search

    override fun initView() {
        EventManager.instance.registerSubscriber(this)

        val manager = FlexboxLayoutManager(this)
        //设置主轴排列方式
        manager.flexDirection = FlexDirection.ROW
        //设置是否换行
        manager.flexWrap = FlexWrap.WRAP
        manager.alignItems = AlignItems.STRETCH
        rlv_hot.layoutManager = manager
        mHotAdapter = HotAdapter(mHotList)
        rlv_hot.adapter = mHotAdapter

        mHisList = LitePal.order("saveTime desc").limit(5).find(SearchListTable::class.java)
        val manager2 = FlexboxLayoutManager(this)
        //设置主轴排列方式
        manager2.flexDirection = FlexDirection.ROW
        //设置是否换行
        manager2.flexWrap = FlexWrap.WRAP
        manager2.alignItems = AlignItems.STRETCH
        mHisAdapter = HistoryAdapter(mHisList)
        rlv_history.layoutManager = manager2
        rlv_history.adapter = mHisAdapter

        rlv_search.layoutManager = LinearLayoutManager(this)
        mSearchAdapter = SearchAdapter(mSearchList, rlv_search)
        rlv_search.adapter = mSearchAdapter

        mSearchAdapter!!.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (mSearchAdapter!!.isLoadingMore) {

                } else {
                    if (loadSize >= COMMENT_SIZE) {
                        mSearchAdapter!!.isLoadingMore = true
                        mSearchList.add(SearchResp.BookBean())
                        mSearchAdapter!!.notifyDataSetChanged()
                        page++
                        getData()
                    }
                }
            }
        })

        AccountManager.getInstance().getHotSearch()
    }

    private fun getData() {
        val str = convertCC(tv_search.text.toString().trim { it <= ' ' })
        AccountManager.getInstance().getSearchBookList("", str, page)
    }

    //繁簡轉換
    fun convertCC(input: String): String {
        return if (TextUtils.isEmpty(input) || input.isEmpty()) "" else ZHConverter.getInstance(
            ZHConverter.SIMPLIFIED
        ).convert(input)
    }

    override fun initData() {
        //输入框
        tv_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' } == "") {
                    refresh.visibility = View.GONE
                    head_hot.visibility = View.VISIBLE
                    head_history.visibility = View.VISIBLE
                    rlv_hot.visibility = View.VISIBLE
                    rlv_history.visibility = View.VISIBLE
                } else {
                    refresh.visibility = View.VISIBLE
                    head_hot.visibility = View.GONE
                    head_history.visibility = View.GONE
                    rlv_hot.visibility = View.GONE
                    rlv_history.visibility = View.GONE
                    refresh.showLoading()
                    page = 1
                    getData()
                }

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        //键盘的搜索
        tv_search.setOnKeyListener { v, keyCode, event ->
            //修改回车键功能
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                mSearchAdapter!!.setHolderType(true)
                saveKey()
                return@setOnKeyListener true
            }
            false
        }

        mHotAdapter!!.setOnItemClickListener { view, pos ->
            mSearchAdapter!!.setHolderType(true)
            refresh.visibility = View.VISIBLE
            tv_search.setText(mHotList[pos])
            saveKey()
        }

        mHisAdapter!!.setOnItemClickListener { view, pos ->
            mSearchAdapter!!.setHolderType(true)
            refresh.visibility = View.VISIBLE
            tv_search.setText(mHisList[pos].key)
            saveKey()
        }

        mSearchAdapter!!.setOnItemClickListener { view, pos ->
            mSearchAdapter!!.setHolderType(true)
            tv_search.setText(mSearchList[pos].title)
            saveKey()
        }
        head_history.setOnClickListener { view ->

            DialogUtils.getInstance().showAlertDialog(
                this,
                getString(R.string.clear_search),
                dialogListener = DialogInterface.OnClickListener { dialog, which ->
                    LitePal.deleteAll(SearchListTable::class.java)
                    mHisList.clear()
                    mHisList.addAll(LitePal.order("saveTime desc").limit(5).find(SearchListTable::class.java))
                    mHisAdapter!!.notifyDataSetChanged()
                })
        }

        tv_cancel.setOnClickListener {
            onBackPressed()
        }

        refresh.setOnReloadingListener { getData() }

    }

    private fun saveKey() {
        if (tv_search.text.toString().trim { it <= ' ' } == "") {
            return
        }
        val searchListTable = SearchListTable()
        searchListTable.key = tv_search.text.toString().trim { it <= ' ' }
        searchListTable.saveTime = System.currentTimeMillis()
        searchListTable.saveOrUpdate("key=?", tv_search.text.toString().trim { it <= ' ' })
        mHisList.clear()
        mHisList.addAll(LitePal.order("saveTime desc").limit(5).find(SearchListTable::class.java))
        mHisAdapter!!.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (refresh.visibility == View.VISIBLE) {
            tv_search.setText("")
            mSearchAdapter!!.setHolderType(false)
            page = 1
        } else {
            super.onBackPressed()
            overridePendingTransition(R.anim.message_fade_in, R.anim.message_fade_out)
        }
    }

    @Subscribe
    fun getHotSearch(event: HotSearchEvent) {
        if (event.isFail) {

        } else {
            mHotList.clear()
            mHotList.addAll(event.result!!.key)
            mHotAdapter!!.notifyDataSetChanged()
        }
    }

    @Subscribe
    fun getSearchList(event: SearchListEvent) {
        refresh.showFinish()
        if (event.isFail) {
            refresh.showError()
        } else {
            loadSize = event.result!!.book.size
            if (mSearchAdapter!!.isLoadingMore) {
                mSearchList.removeAt(mSearchList.size - 1)
                mSearchList.addAll(event.result!!.book)
                mSearchAdapter!!.notifyDataSetChanged()
                mSearchAdapter!!.isLoadingMore = false
            } else {
                mSearchList.clear()
                mSearchList.addAll(event.result!!.book)
                mSearchAdapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.instance.unregisterSubscriber(this)
    }
}
