package com.novel.read.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.novel.read.R
import com.novel.read.adapter.RankListAdapter
import com.novel.read.base.NovelBaseFragment
import com.novel.read.constants.Constant
import com.novel.read.constants.Constant.COMMENT_SIZE
import com.novel.read.http.AccountManager
import com.novel.read.inter.OnLoadMoreListener
import com.novel.read.model.protocol.RankByUpadateResp
import kotlinx.android.synthetic.main.fragment_book_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class BookListFragment : NovelBaseFragment() {

    private var mAdapter: RankListAdapter? = null
    private var mList: MutableList<RankByUpadateResp.BookBean> = ArrayList()
    private var sex: String? = null
    private var dateType: String? = null
    private var type: String? = null
    private var page = 1
    private var loadSize: Int = 0

    override fun getLayoutId(): Int {
        return R.layout.fragment_book_list
    }

    override fun initView() {
        rlv_book_list.layoutManager = LinearLayoutManager(activity)
        mAdapter = RankListAdapter(mList, rlv_book_list)
        rlv_book_list.adapter = mAdapter

        arguments?.let {
            sex = it.getString(Constant.Sex)
            dateType = it.getString(Constant.DateType)
            type = it.getString(Constant.Type)
        }

    }

    override fun initData() {
        getData()

        mAdapter!!.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (mAdapter!!.isLoadingMore) {

                } else {
                    if (loadSize >= COMMENT_SIZE) {
                        mAdapter!!.isLoadingMore = true
                        mList.add(RankByUpadateResp.BookBean())
                        mAdapter!!.notifyDataSetChanged()
                        page++
                        getData()
                    }
                }
            }
        })

    }

    private fun getData() {
        AccountManager.getInstance().getRankList(type, sex, dateType, page.toString(), RankCallBack())
    }

    private inner class RankCallBack : Callback<RankByUpadateResp> {

        override fun onResponse(call: Call<RankByUpadateResp>, response: Response<RankByUpadateResp>) {
            if (response.isSuccessful) {
                if (response.body() != null) {
                    loadSize = response.body()!!.book.size
                    if (mAdapter!!.isLoadingMore) {
                        mList.removeAt(mList.size - 1)
                        mList.addAll(response.body()!!.book)
                        mAdapter!!.notifyDataSetChanged()
                        mAdapter!!.isLoadingMore = false
                    } else {
                        mList.clear()
                        mList.addAll(response.body()!!.book)
                        mAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        }

        override fun onFailure(call: Call<RankByUpadateResp>, t: Throwable) {

        }
    }

    companion object {

        fun newInstance(type: String, dateType: String, sex: String): BookListFragment {
            val args = Bundle()
            args.putString(Constant.Sex, sex)
            args.putString(Constant.DateType, dateType)
            args.putString(Constant.Type, type)
            val fragment = BookListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
