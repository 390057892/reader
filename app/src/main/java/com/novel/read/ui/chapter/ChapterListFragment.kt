package com.novel.read.ui.chapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.VMBaseFragment
import com.novel.read.constant.EventBus
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.help.BookHelp
import com.novel.read.ui.widget.UpLinearLayoutManager
import com.novel.read.ui.widget.VerticalDivider
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.fragment_chapter_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.sdk27.listeners.onClick

class ChapterListFragment : VMBaseFragment<ChapterListViewModel>(R.layout.fragment_chapter_list),
    ChapterListViewModel.ChapterListCallBack,ChapterListAdapter.Callback {

    lateinit var adapter: ChapterListAdapter
    private var durChapterIndex = 0
    private lateinit var mLayoutManager: UpLinearLayoutManager
    private var tocLiveData: MutableLiveData<List<BookChapter>>? = MutableLiveData()
    private var scrollToDurChapter = false

    override val viewModel: ChapterListViewModel
        get() = getViewModelOfActivity(ChapterListViewModel::class.java)

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.chapterCallBack = this
        val bbg = bottomBackground
        val btc = requireContext().getPrimaryTextColor(ColorUtils.isColorLight(bbg))
        ll_chapter_base_info.setBackgroundColor(bbg)
        tv_current_chapter_info.setTextColor(btc)
        iv_chapter_top.setColorFilter(btc)
        iv_chapter_bottom.setColorFilter(btc)
        initRecyclerView()
        initView()
        initBook()
    }

    private fun initRecyclerView() {
        adapter = ChapterListAdapter(this)
        mLayoutManager = UpLinearLayoutManager(requireContext())
        recycler_view.layoutManager = mLayoutManager
        recycler_view.addItemDecoration(VerticalDivider(requireContext()))
        recycler_view.adapter = adapter
    }

    private fun initView() {
        iv_chapter_top.onClick { mLayoutManager.scrollToPositionWithOffset(0, 0) }
        iv_chapter_bottom.onClick {
            if (adapter.itemCount > 0) {
                mLayoutManager.scrollToPositionWithOffset(adapter.itemCount - 1, 0)
            }
        }
        tv_current_chapter_info.onClick {
            mLayoutManager.scrollToPositionWithOffset(durChapterIndex, 0)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun initBook() {
        launch {
            initDoc()
            viewModel.book?.let {
                durChapterIndex = it.durChapterIndex
                tv_current_chapter_info.text =
                    "${it.durChapterTitle}(${it.durChapterIndex + 1}/${tocLiveData?.value?.size})"
                initCacheFileNames(it)
            }
        }
    }

    private fun initDoc() {
        tocLiveData?.removeObservers(this@ChapterListFragment)
        tocLiveData?.value = App.db.getChapterDao().observeByBook(viewModel.bookId)
        tocLiveData?.observe(viewLifecycleOwner) {
            adapter.setList(it)
            if (!scrollToDurChapter) {
                mLayoutManager.scrollToPositionWithOffset(durChapterIndex, 0)
                scrollToDurChapter = true
            }
        }
    }

    private fun initCacheFileNames(book: Book) {
        launch(Dispatchers.IO) {
            adapter.cacheFileNames.addAll(BookHelp.getChapterFiles(book))
            withContext(Dispatchers.Main) {
                adapter.notifyItemRangeChanged(0, adapter.itemCount, true)
            }
        }
    }

    override fun observeLiveBus() {
        observeEvent<BookChapter>(EventBus.SAVE_CONTENT) { chapter ->
            viewModel.book?.bookId?.let { bookId ->
                if (chapter.bookId == bookId) {
                    adapter.cacheFileNames.add(BookHelp.formatChapterName(chapter))
                    adapter.notifyItemChanged(chapter.chapterIndex, true)
                }
            }
        }
    }

    override fun startChapterListSearch(newText: String?) {
        if (newText.isNullOrBlank()) {
            initDoc()
        } else {
            tocLiveData?.removeObservers(this)
            tocLiveData?.value = App.db.getChapterDao().liveDataSearch(viewModel.bookId, newText)
            tocLiveData?.observe(viewLifecycleOwner, {
                adapter.setList(it)
            })
        }
    }

    override val isLocalBook: Boolean
        get() = viewModel.book?.isLocalBook() == true

    override fun durChapterIndex(): Int {
        return durChapterIndex
    }

    override fun openChapter(bookChapter: BookChapter) {
        activity?.setResult(Activity.RESULT_OK, Intent().putExtra("index", bookChapter.chapterIndex))
        activity?.finish()
    }
}