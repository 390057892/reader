package com.novel.read.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.UIData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.EventBus
import com.novel.read.data.model.AppUpdateResp
import com.novel.read.databinding.ActivityMainBinding
import com.novel.read.help.AppConfig
import com.novel.read.lib.ATH
import com.novel.read.service.BaseReadAloudService
import com.novel.read.ui.main.bookshelf.BookshelfFragment
import com.novel.read.ui.main.mail.MailFragment
import com.novel.read.ui.main.my.MyFragment
import com.novel.read.utils.ext.*
import org.jetbrains.anko.toast

class MainActivity : VMBaseActivity<ActivityMainBinding, MainViewModel>(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemReselectedListener,
    ViewPager.OnPageChangeListener by ViewPager.SimpleOnPageChangeListener() {

    override val viewModel: MainViewModel
        get() = getViewModel(MainViewModel::class.java)

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private var bookshelfReselected: Long = 0
    private var exitTime: Long = 0
    private var pagePosition = 0
    private val fragmentMap = hashMapOf<Int, Fragment>()

    override fun onActivityCreated(savedInstanceState: Bundle?) = with(binding) {
        ATH.applyEdgeEffectColor(viewPagerMain)
        ATH.applyBottomNavigationColor(bottomNavigationView)

        viewPagerMain.offscreenPageLimit = 3
        viewPagerMain.adapter = TabFragmentPageAdapter(supportFragmentManager)
        viewPagerMain.addOnPageChangeListener(this@MainActivity)
        bottomNavigationView.elevation =
            if (AppConfig.elevation < 0) elevation else AppConfig.elevation.toFloat()
        bottomNavigationView.setOnNavigationItemSelectedListener(this@MainActivity)
        bottomNavigationView.setOnNavigationItemReselectedListener(this@MainActivity)
        initData()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.appUpdate()
        //自动更新书籍
        binding.viewPagerMain.postDelayed({
            viewModel.upAllBookToc()
        }, 1000)
    }


    private fun initData() {
        viewModel.appResp.observe(this) {
            updateApk(it)
        }
    }

    override fun onPageSelected(position: Int) = with(binding) {
        viewPagerMain.hideSoftInput()
        pagePosition = position
        when (position) {
            0, 1, 2 -> bottomNavigationView.menu.getItem(position).isChecked = true
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        event?.let {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> if (event.isTracking && !event.isCanceled) {
                    if (pagePosition != 0) {
                        binding.viewPagerMain.currentItem = 0
                        return true
                    }
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        toast(R.string.double_click_exit)
                        exitTime = System.currentTimeMillis()
                    } else {
                        if (BaseReadAloudService.pause) {
                            finish()
                        } else {
                            moveTaskToBack(true)
                        }
                    }
                    return true
                }
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = with(binding) {
        when (item.itemId) {
            R.id.menu_mail -> viewPagerMain.setCurrentItem(0, false)
            R.id.menu_bookshelf -> viewPagerMain.setCurrentItem(1, false)
            R.id.menu_my_config -> viewPagerMain.setCurrentItem(2, false)
        }
        return false
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        when (item.itemId) {
            R.id.menu_bookshelf -> {
                if (System.currentTimeMillis() - bookshelfReselected > 300) {
                    bookshelfReselected = System.currentTimeMillis()
                } else {
                    (fragmentMap[0] as? BookshelfFragment)?.gotoTop()
                }
            }
        }
    }


    private inner class TabFragmentPageAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private fun getId(position: Int): Int {
            return position
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        override fun getItem(position: Int): Fragment {
            return when (getId(position)) {
                0 -> fragmentMap[0] ?: MailFragment()
                1 -> fragmentMap[1] ?: BookshelfFragment()
                else -> fragmentMap[2] ?: MyFragment()
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment = super.instantiateItem(container, position) as Fragment
            fragmentMap[getId(position)] = fragment
            return fragment
        }

    }

    override fun observeLiveBus() {
        observeEvent<String>(EventBus.RECREATE) {
            recreate()
        }
    }


    private fun updateApk(resp: AppUpdateResp?) {
        val versionBean = resp?.appEdition
        if (versionBean != null) {
            val builder = AllenVersionChecker
                .getInstance()
                .downloadOnly(
                    UIData.create()
                        .setTitle(getString(R.string.new_version, versionBean.editionCode))
                        .setContent(versionBean.upgradeContent)
                        .setDownloadUrl(versionBean.fileUrl)
                )
            if (versionBean.forceUpdate == 1) { //强制更新
                builder.setForceUpdateListener(::finish)
            }
            builder.executeMission(this)
        }
    }

}