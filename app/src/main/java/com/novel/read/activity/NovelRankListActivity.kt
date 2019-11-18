package com.novel.read.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.novel.read.R
import com.novel.read.adapter.RankListAdapter
import com.novel.read.base.NovelBaseActivity
import com.novel.read.constants.Constant
import com.novel.read.constants.Constant.COMMENT_SIZE
import com.novel.read.http.AccountManager
import com.novel.read.inter.OnLoadMoreListener
import com.novel.read.model.protocol.RankByUpdateResp
import kotlinx.android.synthetic.main.activity_rank_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * 推荐fragment中点击更多跳转来的。
 */
class NovelRankListActivity : NovelBaseActivity() {

    private lateinit var mAdapter: RankListAdapter
    private var mList: MutableList<RankByUpdateResp.BookBean> = ArrayList()
    private var page = 1
    private var loadSize: Int = 0
    private var type: String = ""
    private var sex: String = ""

    override val layoutId: Int get() = R.layout.activity_rank_list

    override fun initView() {
        rlv_book_list.layoutManager = LinearLayoutManager(this)
        mAdapter = RankListAdapter(mList, rlv_book_list)
        rlv_book_list.adapter = mAdapter
        sex = intent.getStringExtra(Constant.Sex)
        type = intent.getStringExtra(Constant.Type)
        when (type) {
            Constant.ListType.Human -> toolbar.title = getString(R.string.popular_selection)
            Constant.ListType.EditRecommend -> toolbar.title = getString(R.string.edit_recommend)
            Constant.ListType.HotSearch -> toolbar.title = getString(R.string.hot_search)
        }
        getData()
    }

    override fun initData() {
        toolbar.setNavigationOnClickListener { finish() }

        mAdapter.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (mAdapter.isLoadingMore) {

                } else {
                    if (loadSize >= COMMENT_SIZE) {
                        mAdapter.isLoadingMore = true
                        mAdapter.notifyDataSetChanged()
                        page++
                        getData()
                    }
                }
            }
        })
    }

    private fun getData() {
        AccountManager.getInstance().getRankList(type, sex, Constant.DateTyp.Week, page.toString(), RankCallBack())
    }

    private inner class RankCallBack : Callback<RankByUpdateResp> {

        override fun onResponse(call: Call<RankByUpdateResp>, response: Response<RankByUpdateResp>) {
            if (response.isSuccessful) {
                if (response.body() != null) {
                    loadSize = response.body()!!.book.size
                    if (mAdapter.isLoadingMore) {
                        mAdapter.isLoadingMore = false
                        mList.addAll(response.body()!!.book)
                        mAdapter.notifyDataSetChanged()
                    } else {
                        mList.clear()
                        mList.addAll(response.body()!!.book)
                        mAdapter.notifyDataSetChanged()
                    }

                }
            }
        }

        override fun onFailure(call: Call<RankByUpdateResp>, t: Throwable) {

        }
    }
}
