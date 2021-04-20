package com.novel.read.ui.chapter

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.databinding.ActivityChapterListBinding
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.*

class ChapterListActivity : VMBaseActivity<ActivityChapterListBinding,ChapterListViewModel>() {
    override val viewModel: ChapterListViewModel
        get() = getViewModel(ChapterListViewModel::class.java)

    override fun getViewBinding(): ActivityChapterListBinding {
        return ActivityChapterListBinding.inflate(layoutInflater)
    }
    private lateinit var tabLayout: TabLayout
    private var searchView: SearchView? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        tabLayout = binding.titleBar.findViewById(R.id.tab_layout)
        tabLayout.isTabIndicatorFullWidth = false
        tabLayout.setSelectedTabIndicatorColor(accentColor)
        intent.getStringExtra("bookId")?.let {
            viewModel.initBook(it) {
                binding.viewPager.adapter = TabFragmentPageAdapter(supportFragmentManager)
                tabLayout.setupWithViewPager(binding.viewPager)
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
            tabLayout.visible()
            false
        }
        searchView?.setOnSearchClickListener { tabLayout.gone() }
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (tabLayout.selectedTabPosition == 1) {
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
        if (tabLayout.isGone) {
            searchView?.onActionViewCollapsed()
            tabLayout.visible()
        } else {
            super.onBackPressed()
        }
    }
}