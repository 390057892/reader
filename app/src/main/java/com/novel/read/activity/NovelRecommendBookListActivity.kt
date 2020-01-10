package com.novel.read.activity

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.novel.read.R
import com.novel.read.adapter.ViewPageAdapter
import com.novel.read.base.NovelBaseActivity
import com.novel.read.constants.Constant
import com.novel.read.fragment.BookListFragment
import com.novel.read.widget.VpTabLayout
import kotlinx.android.synthetic.main.activity_recommend_book_list.*
import java.util.*

class NovelRecommendBookListActivity(override val layoutId: Int= R.layout.activity_recommend_book_list) : NovelBaseActivity() {

    override fun initView() {
        val fragmentList = ArrayList<Fragment>()
        val sex = intent.getStringExtra(Constant.Sex)
        val type = intent.getStringExtra(Constant.Type)
        when (type) {
            Constant.ListType.Human -> toolbar.title = getString(R.string.popular_selection)
            Constant.ListType.EditRecommend -> toolbar.title = getString(R.string.edit_recommend)
            Constant.ListType.HotSearch -> toolbar.title = getString(R.string.hot_search)
        }
        val generalFragment = BookListFragment.newInstance(type, Constant.DateTyp.General, sex)
        val monthFragment = BookListFragment.newInstance(type, Constant.DateTyp.Month, sex)
        val weekFragment = BookListFragment.newInstance(type, Constant.DateTyp.Week, sex)
        fragmentList.add(generalFragment)
        fragmentList.add(monthFragment)
        fragmentList.add(weekFragment)
        val pageAdapter = ViewPageAdapter(supportFragmentManager, fragmentList)
        vp_recommend_type.adapter = pageAdapter
        vp_recommend_type.offscreenPageLimit = 2
        vp_recommend_type.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                vp_tab.setAnim(position, vp_recommend_type)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun initData() {
        toolbar!!.setNavigationOnClickListener { finish() }

        vp_tab.setOnTabBtnClickListener(object : VpTabLayout.OnTabClickListener {
            override fun onTabBtnClick(var1: VpTabLayout.CommonTabBtn, var2: View) {
                when (var1) {
                    VpTabLayout.CommonTabBtn.ONE -> vp_tab.setAnim(0, vp_recommend_type)
                    VpTabLayout.CommonTabBtn.TWO -> vp_tab.setAnim(1, vp_recommend_type)
                    VpTabLayout.CommonTabBtn.THREE -> vp_tab.setAnim(2, vp_recommend_type)
                }
            }
        })

    }
}
