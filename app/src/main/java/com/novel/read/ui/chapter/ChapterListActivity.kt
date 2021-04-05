package com.novel.read.ui.chapter

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.activity_chapter_list.*
import kotlinx.android.synthetic.main.view_tab_layout.*


class ChapterListActivity : VMBaseActivity<ChapterListViewModel>(R.layout.activity_chapter_list) {
    override val viewModel: ChapterListViewModel
        get() = getViewModel(ChapterListViewModel::class.java)

    private var searchView: SearchView? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        tab_layout.isTabIndicatorFullWidth = false
        tab_layout.setSelectedTabIndicatorColor(accentColor)
        intent.getStringExtra("bookId")?.let {
            viewModel.initBook(it) {
                view_pager.adapter = TabFragmentPageAdapter(supportFragmentManager)
                tab_layout.setupWithViewPager(view_pager)
            }
        }
    }

    override fun onCompatCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_view, menu)
        val search = menu.findItem(R.id.menu_search)
        searchView = search.actionView as SearchView
        ATH.setTint(searchView!!, primaryTextColor)
        searchView?.maxWidth = resources.displayMetrics.widthPixels
        searchView?.onActionViewCollapsed()
        searchView?.setOnCloseListener {
            tab_layout.visible()
            false
        }
        searchView?.setOnSearchClickListener { tab_layout.gone() }
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (tab_layout.selectedTabPosition == 1) {
                    viewModel.startBookmarkSearch(newText)
                } else {
                    viewModel.startChapterListSearch(newText)
                }
                return false
            }
        })
        return super.onCompatCreateOptionsMenu(menu)
    }

    private inner class TabFragmentPageAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                1 -> BookmarkFragment()
                else -> ChapterListFragment()
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                1 -> getString(R.string.bookmark)
                else -> getString(R.string.chapter_list)
            }
        }

    }

    override fun onBackPressed() {
        if (tab_layout.isGone) {
            searchView?.onActionViewCollapsed()
            tab_layout.visible()
        } else {
            super.onBackPressed()
        }
    }
}