package com.novel.read.ui.info

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.AppConst
import com.novel.read.constant.EventBus
import com.novel.read.constant.IntentAction
import com.novel.read.data.db.entity.Book
import com.novel.read.help.AppConfig
import com.novel.read.help.IntentDataHelp
import com.novel.read.ui.read.ReadBookActivity
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.activity_book_info.*
import org.jetbrains.anko.sdk27.listeners.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class BookInfoActivity : VMBaseActivity<BookInfoViewModel>(R.layout.activity_book_info) {

    private lateinit var adapter: BookInfoAdapter
    private val requestCodeRead = 432

    override val viewModel: BookInfoViewModel
        get() = getViewModel(BookInfoViewModel::class.java)

    companion object {
        fun actionBookInfo(context: Context, bookId: Long, bookTypeId: Int?) {
            context.startActivity<BookInfoActivity>(
                Pair(IntentAction.bookId, bookId),
                Pair(IntentAction.bookTypeId, bookTypeId),
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel.initData(intent)
        viewModel.getRecommend(intent)
        tv_shelf.setTextColor(getPrimaryTextColor(ColorUtils.isColorLight(bottomBackground)))
        initRecycleView()
        initData()
        initClick()
    }

    private fun initRecycleView() {
        rlv_recommend.layoutManager = GridLayoutManager(this, 4)
        adapter = BookInfoAdapter()
        rlv_recommend.adapter = adapter
    }

    private fun initData() {
        val dividerColor = if (AppConfig.isNightTheme) {
            ColorUtils.shiftColor(backgroundColor, 1.05f)
        } else {
            ColorUtils.shiftColor(backgroundColor, 0.95f)
        }
        preference_divider_above.setBackgroundColor(dividerColor)
        viewModel.bookResp.observe(this) {
            iv_book.load(it.coverImageUrl, it.bookName, it.authorName)
            tv_book_name.text = it.bookName
            tv_book_author.text = it.authorName
            tv_key.text = it.lastUpdateChapterDate
            tv_status.text = it.categoryName
            tv_word.text = getString(R.string.book_word, it.wordCount / 10000)

            tv_tro.text = it.introduction

            upTvBookshelf()
        }
        viewModel.bookListResp.observe(this) {
            adapter.setList(it)
        }
        viewModel.status.observe(this) {
            when (it) {
                AppConst.loading -> {
                    refresh.showLoading()
                }
                AppConst.complete -> {
                    refresh.showFinish()
                }
                else -> {
                    refresh.showError()
                }
            }
        }
    }

    private fun initClick() {
        tv_shelf.onClick {
            if (viewModel.inBookshelf) {
                deleteBook()
            } else {
                viewModel.addToBookshelf {
                    upTvBookshelf()
                }
            }

        }

        tv_read.onClick {
            viewModel.bookData.value?.let {
                readBook(it)
            }

        }

        refresh.setOnReloadingListener {
            viewModel.initData(intent)
            viewModel.getRecommend(intent)
        }
    }

    private fun readBook(book: Book) {
        if (!viewModel.inBookshelf) {
            startReadActivity(book)
        } else {
            viewModel.saveBook {
                startReadActivity(book)
            }
        }
        postEvent(EventBus.UP_BOOK, 0L)
    }

    private fun startReadActivity(book: Book) {
        startActivityForResult<ReadBookActivity>(
            requestCodeRead,
            Pair("bookId", book.bookId.toString()),
            Pair("inBookshelf", viewModel.inBookshelf),
            Pair("key", IntentDataHelp.putData(book))
        )
    }

    private fun upTvBookshelf() {
        if (viewModel.inBookshelf) {
            tv_shelf.text = getString(R.string.remove_from_bookshelf)
        } else {
            tv_shelf.text = getString(R.string.add_to_shelf)
        }
        postEvent(EventBus.UP_BOOK, 0L)
    }

    @SuppressLint("InflateParams")
    private fun deleteBook() {
        viewModel.delBook {
            upTvBookshelf()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestCodeRead -> if (resultCode == Activity.RESULT_OK) {
                viewModel.inBookshelf = true
                viewModel.saveBook {
                    upTvBookshelf()
                }
            }
        }
    }
}