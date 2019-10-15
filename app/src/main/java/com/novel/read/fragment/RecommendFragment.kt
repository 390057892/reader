package com.novel.read.fragment

import android.os.Bundle
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.activity.NovelSearchActivity
import com.novel.read.base.NovelBaseFragment
import kotlinx.android.synthetic.main.title_recommend.*

/**
 * create by zlj on 2019/6/10
 * describe:
 */
class RecommendFragment : NovelBaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_recommend
    }

    override fun initView() {
        EventManager.instance.registerSubscriber(this)
        //        List<Fragment> fragmentList = new ArrayList<>();
        //        ManFragment manFragment = ManFragment.newInstance(Constant.GenderType.Man);
        //        WomanFragment womanFragment = WomanFragment.newInstance(Constant.GenderType.Woman);
        //        fragmentList.add(manFragment);
        //        fragmentList.add(womanFragment);
    }

    override fun initData() {
        tv_search.setOnClickListener {
            toActivity(NovelSearchActivity::class.java)
            activity!!.overridePendingTransition(
                R.anim.message_fade_in,
                R.anim.message_fade_out
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.instance.unregisterSubscriber(this)
    }

    companion object {
        fun newInstance(): RecommendFragment {
            val args = Bundle()
            val fragment = RecommendFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
