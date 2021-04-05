package com.novel.read.ui.chapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.VMBaseFragment
import com.novel.read.data.db.entity.Bookmark
import com.novel.read.lib.ATH
import com.novel.read.lib.dialogs.alert
import com.novel.read.lib.dialogs.customView
import com.novel.read.lib.dialogs.noButton
import com.novel.read.lib.dialogs.yesButton
import com.novel.read.ui.widget.VerticalDivider
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.dialog_edit_text.view.*
import kotlinx.android.synthetic.main.fragment_bookmark.*

class BookmarkFragment : VMBaseFragment<ChapterListViewModel>(R.layout.fragment_bookmark),
    ChapterListViewModel.BookmarkCallBack {

    override val viewModel: ChapterListViewModel
        get() = getViewModelOfActivity(ChapterListViewModel::class.java)

    private lateinit var adapter: BookMarkAdapter
    private var bookmarkLiveData =  MutableLiveData<List<Bookmark>>()
    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.bookMarkCallBack = this
        initRecyclerView()
        initData()
    }

    private fun initRecyclerView() {
        ATH.applyEdgeEffectColor(recycler_view)
        adapter = BookMarkAdapter()
        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        recycler_view.addItemDecoration(VerticalDivider(requireContext()))
        recycler_view.adapter = adapter
    }

    private fun initData() {
        viewModel.book?.let { book ->
            bookmarkLiveData.removeObservers(viewLifecycleOwner)
            bookmarkLiveData.value = App.db.getBookMarkDao().observeByBook(book.bookId)
            bookmarkLiveData.observe(viewLifecycleOwner) {
                adapter.setList(it)
            }
        }

        adapter.setOnItemClickListener { adapter, view, position ->
            val bookmarkData = Intent()
            val bookmark: Bookmark =  bookmarkLiveData.value!![position]
            bookmarkData.putExtra("index", bookmark.chapterIndex)
            bookmarkData.putExtra("pageIndex", bookmark.pageIndex)
            activity?.setResult(Activity.RESULT_OK, bookmarkData)
            activity?.finish()
        }

        adapter.setOnItemLongClickListener { adapter, view, position ->
            val bookmark: Bookmark =  bookmarkLiveData.value!![position]
            viewModel.book?.let { book ->
                requireContext().alert(R.string.bookmark) {
                    var editText: EditText? = null
                    message = book.bookName + " • " + bookmark.chapterName
                    customView {
                        layoutInflater.inflate(R.layout.dialog_edit_text, null).apply {
                            editText = edit_view.apply {
                                setHint(R.string.note_content)
                                setText(bookmark.content)
                            }
                        }
                    }
                    yesButton {
                        editText?.text?.toString()?.let { editContent ->
                            bookmark.content = editContent
                            App.db.getBookMarkDao().update(bookmark)
                            bookmarkLiveData.value=App.db.getBookMarkDao().observeByBook(book.bookId)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    noButton()
                    neutralButton(R.string.delete) {
                        App.db.getBookMarkDao().delete(bookmark)
                        bookmarkLiveData.value=App.db.getBookMarkDao().observeByBook(book.bookId)
                        adapter.notifyDataSetChanged()
                    }
                }.show().applyTint().requestInputMethod()
            }
            true
        }
    }

    override fun startBookmarkSearch(newText: String?) {
        if (newText.isNullOrBlank()) {
            initData()
        } else {
            bookmarkLiveData.removeObservers(viewLifecycleOwner)
            bookmarkLiveData.value =
                App.db.getBookMarkDao().liveDataSearch(
                    viewModel.bookId,
                    newText
                )
            bookmarkLiveData.observe(viewLifecycleOwner, { adapter.setList(it) })
        }
    }
}