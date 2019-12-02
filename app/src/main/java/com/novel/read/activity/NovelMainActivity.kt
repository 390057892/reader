package com.novel.read.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.base.NovelBaseActivity
import com.novel.read.constants.Constant
import com.novel.read.event.*
import com.novel.read.fragment.BookFragment
import com.novel.read.fragment.MoreFragment
import com.novel.read.fragment.RecommendFragment
import com.novel.read.fragment.StackFragment
import com.novel.read.http.AccountManager
import com.novel.read.model.db.dbManage.BookRepository
import com.novel.read.showToast
import com.novel.read.utlis.SpUtil
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_main.*

class NovelMainActivity : NovelBaseActivity() {

    private lateinit var mCurrentFrag: Fragment
    private lateinit var mMainFragment: BookFragment
    private lateinit var mRecommendFragment: RecommendFragment
    private lateinit var mStackFragment: StackFragment
    private lateinit var mMoreFragment: MoreFragment

    //记录用户首次点击返回键的时间
    private var firstTime: Long = 0

    override val layoutId: Int get() =  R.layout.activity_main

    override fun initView() {
        mCurrentFrag = Fragment()
        mMainFragment = BookFragment.newInstance()
        mRecommendFragment = RecommendFragment.newInstance()
        mStackFragment = StackFragment.newInstance()
        mMoreFragment = MoreFragment.newInstance()
        AccountManager.getInstance().login(this)
    }

    override fun initData() {
        bottom_bar.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.tab_one -> {
                    switchFragment(mMainFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab_two -> {
                    switchFragment(mRecommendFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab_three -> {
                    switchFragment(mStackFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab_four -> {
                    switchFragment(mMoreFragment)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        if (BookRepository.getInstance().collBooks.size > 0) {
            switchFragment(mMainFragment)
        } else {
            bottom_bar.selectedItemId = R.id.tab_two
        }
    }

    private fun switchFragment(targetFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        if (!targetFragment.isAdded) {
            transaction.hide(mCurrentFrag)
            transaction.add(R.id.fl_content, targetFragment, targetFragment.javaClass.name)
        } else {
            transaction.hide(mCurrentFrag).show(targetFragment)
        }
        mCurrentFrag = targetFragment
        transaction.commit()
    }

    override fun onResume() {
        super.onResume()
        EventManager.instance.registerSubscriber(this)
    }

    override fun onPause() {
        super.onPause()
        EventManager.instance.unregisterSubscriber(this)
    }

    @Subscribe
    fun login(event: LoginEvent) {
        if (event.isFail) {
            Log.e("NovelMainActivity", "login: " + event.er!!.msg)
        } else {
            SpUtil.setStringValue(Constant.Uid, event.result!!.uid.toString())
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (!isVisible(bottom_bar)) {
                bottom_bar.visibility = View.VISIBLE
                mMainFragment.updateBook(UpdateBookEvent())
            } else {
                val secondTime = System.currentTimeMillis()
                if (secondTime - firstTime > 1000) {
                    firstTime = secondTime
                    showToast("再次点击退出界面")
                } else {
                    finish()
                }
            }
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    @Subscribe
    fun setBottomBar(event: HideBottomBarEvent) {
        if (event.result!!) {
            bottom_bar.visibility = View.GONE
        } else {
            bottom_bar.visibility = View.VISIBLE
        }
    }

    @Subscribe
    fun toRecommendFragment(event: SwitchFragmentEvent) {
        bottom_bar.selectedItemId = R.id.tab_two
    }

    companion object {
        fun reStart(context: Context) {
            val intent = Intent(context, NovelMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
