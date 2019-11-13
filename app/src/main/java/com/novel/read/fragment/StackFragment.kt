package com.novel.read.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.activity.NovelSearchActivity
import com.novel.read.adapter.StackAdapter
import com.novel.read.base.NovelBaseFragment
import com.novel.read.event.GetCategoryTypeEvent
import com.novel.read.http.AccountManager
import com.novel.read.model.protocol.CategoryTypeResp
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.fragment_stack.*
import kotlinx.android.synthetic.main.title_stack.*
import java.util.*

/**
 * create by zlj on 2019/6/10
 * describe:
 */
class StackFragment : NovelBaseFragment() {

    private lateinit var mAdapter: StackAdapter
    private var mList: MutableList<CategoryTypeResp.CategoryBean> =ArrayList()

    override fun getLayoutId(): Int {
        return R.layout.fragment_stack
    }

    override fun initView() {
        EventManager.instance.registerSubscriber(this)
        rlv_book_type.layoutManager = GridLayoutManager(activity, 2)
        mAdapter = StackAdapter(mList)
        rlv_book_type.adapter = mAdapter

    }

    override fun initData() {
        refresh.showLoading()
        getData()
        refresh.setOnReloadingListener { getData() }

        tv_search.setOnClickListener {
            toActivity(NovelSearchActivity::class.java)
            activity!!.overridePendingTransition(R.anim.message_fade_in, R.anim.message_fade_out)
        }
    }

    private fun getData() {
        AccountManager.getInstance().getCategoryType()
    }

    @Subscribe
    fun getCategoryType(event: GetCategoryTypeEvent) {
        refresh.showFinish()
        if (event.isFail) {
            refresh.showError()
        } else {
            mList.clear()
            mList.addAll(event.result!!.category)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.instance.unregisterSubscriber(this)
    }

    companion object {
        fun newInstance(): StackFragment {
            val args = Bundle()
            val fragment = StackFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
