package com.novel.read.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.adapter.EditRecommendAdapter
import com.novel.read.adapter.HumanAdapter
import com.novel.read.adapter.RankAdapter
import com.novel.read.base.NovelBaseFragment
import com.novel.read.constants.Constant
import com.novel.read.http.AccountManager
import com.novel.read.model.protocol.RecommendListResp
import kotlinx.android.synthetic.main.fragment_man.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ManFragment : NovelBaseFragment() {

    private var mHumanAdapter: HumanAdapter? = null
    private val mHumanList = ArrayList<RecommendListResp.ListBean>()
    private var mEditAdapter: EditRecommendAdapter? = null
    private val mEditList = ArrayList<RecommendListResp.ListBean>()
    private var mRankAdapter: RankAdapter? = null
    private val mRankList = ArrayList<RecommendListResp.ListBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_man
    }

    override fun initView() {
        EventManager.instance.registerSubscriber(this)

        rlv_pop.layoutManager = GridLayoutManager(activity, 3)
        mHumanAdapter = HumanAdapter(mHumanList)
        rlv_pop.adapter = mHumanAdapter

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rlv_recommend.layoutManager = linearLayoutManager
        mEditAdapter = EditRecommendAdapter(mEditList)
        rlv_recommend.adapter = mEditAdapter

        rlv_update.layoutManager = GridLayoutManager(activity, 3)
        mRankAdapter = RankAdapter(mRankList)
        rlv_update.adapter = mRankAdapter
    }

    override fun initData() {
        swipe.showLoading()
        getData()
        swipe.setOnReloadingListener { getData() }
    }

    private fun getData() {
        AccountManager.getInstance().getRecommendList(Constant.ListType.Human, HumanCallBack())
        AccountManager.getInstance()
            .getRecommendList(Constant.ListType.EditRecommend, EditCallBack())
        AccountManager.getInstance()
            .getRecommendList(Constant.ListType.HotSearch, HotSearchCallBack())
    }

    private inner class HumanCallBack : Callback<RecommendListResp> {

        override fun onResponse(
            call: Call<RecommendListResp>,
            response: Response<RecommendListResp>
        ) {
            if (response.isSuccessful && response.body() != null) {
                mHumanList.clear()
                mHumanList.addAll(response.body()!!.list)
                mHumanAdapter!!.notifyDataSetChanged()
            } else {
                swipe.showError()
            }
        }

        override fun onFailure(call: Call<RecommendListResp>, t: Throwable) {

        }
    }

    private inner class EditCallBack : Callback<RecommendListResp> {

        override fun onResponse(
            call: Call<RecommendListResp>,
            response: Response<RecommendListResp>
        ) {
            if (response.isSuccessful && response.body() != null) {
                mEditList.clear()
                mEditList.addAll(response.body()!!.list)
                mEditAdapter!!.notifyDataSetChanged()

            } else {
                swipe.showError()
            }
        }

        override fun onFailure(call: Call<RecommendListResp>, t: Throwable) {

        }
    }

    private inner class HotSearchCallBack : Callback<RecommendListResp> {

        override fun onResponse(
            call: Call<RecommendListResp>,
            response: Response<RecommendListResp>
        ) {
            swipe.showFinish()
            if (response.isSuccessful && response.body() != null) {
                mRankList.clear()
                mRankList.addAll(response.body()!!.list)
                mRankAdapter!!.notifyDataSetChanged()
            } else {
                swipe.showError()
            }
        }

        override fun onFailure(call: Call<RecommendListResp>, t: Throwable) {

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventManager.instance.unregisterSubscriber(this)
    }

    companion object {

        fun newInstance(sex: String): ManFragment {
            val args = Bundle()
            args.putString(Constant.Sex, sex)
            val fragment = ManFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
