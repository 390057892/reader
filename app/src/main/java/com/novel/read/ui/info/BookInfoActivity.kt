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
import com.novel.read.databinding.ActivityBookInfoBinding
import com.novel.read.help.AppConfig
import com.novel.read.help.IntentDataHelp
import com.novel.read.ui.read.ReadBookActivity
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.ext.*
import org.jetbrains.anko.sdk27.listeners.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class BookInfoActivity : VMBaseActivity<ActivityBookInfoBinding,BookInfoViewModel>() {

    private lateinit var adapter: BookInfoAdapter
    private val requestCodeRead = 432

    override val viewModel: BookInfoViewModel
        get() = getViewModel(BookInfoViewModel::class.java)

    override fun getViewBinding(): ActivityBookInfoBinding {
        return ActivityBookInfoBinding.inflate(layoutInflater)
    }
    
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
        binding.tvShelf.setTextColor(getPrimaryTextColor(ColorUtils.isColorLight(bottomBackground)))
        initRecycleView()
        initData()
        initClick()
    }

    private fun initRecycleView() {
        binding.rlvRecommend.layoutManager = GridLayoutManager(this, 4)
        adapter = BookInfoAdapter()
        binding.rlvRecommend.adapter = adapter
    }

    private fun initData() {
        val dividerColor = if (AppConfig.isNightTheme) {
            ColorUtils.shiftColor(backgroundColor, 1.05f)
        } else {
            ColorUtils.shiftColor(backgroundColor, 0.95f)
        }

        binding.preferenceDividerAbove.setBackgroundColor(dividerColor)
        viewModel.bookResp.observe(this) {
            binding.ivBook.load(it.coverImageUrl, it.bookName, it.authorName)
            binding.tvBookName.text = it.bookName
            binding.tvBookAuthor.text = it.authorName
            binding.tvKey.text = it.lastUpdateChapterDate
            binding.tvStatus.text = it.categoryName
            binding.tvWord.text = getString(R.string.book_word, it.wordCount / 10000)
            binding.tvTro.text = it.introduction
            upTvBookshelf()
        }
        viewModel.bookListResp.observe(this) {
            adapter.setList(it)
        }
        viewModel.status.observe(this) {
            when (it) {
                AppConst.loading -> {
                    binding.refresh.showLoading()
                }
                AppConst.complete -> {
                    binding.refresh.showFinish()
                }
                else -> {
                    binding.refresh.showError()
                }
            }
        }
    }

    private fun initClick() {
        binding.tvShelf.onClick {
            if (viewModel.inBookshelf) {
                deleteBook()
            } else {
                viewModel.addToBookshelf {
                    upTvBookshelf()
                }
            }

        }

        binding.tvRead.onClick {
            viewModel.bookData.value?.let {
                readBook(it)
            }

        }

        binding.refresh.setOnReloadingListener {
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
            binding.tvShelf.text = getString(R.string.remove_from_bookshelf)
        } else {
            binding.tvShelf.text = getString(R.string.add_to_shelf)
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