package com.novel.read.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.BaseViewModel
import com.novel.read.constant.BookType
import com.novel.read.constant.EventBus
import com.novel.read.constant.PreferKey
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.data.model.AppUpdateResp
import com.novel.read.data.model.ChapterResp
import com.novel.read.network.repository.HomeRepository
import com.novel.read.service.help.ReadBook
import com.novel.read.utils.StringUtils
import com.novel.read.utils.ext.*
import com.novel.read.help.coroutine.Coroutine
import com.novel.read.network.repository.BookRepository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class MainViewModel(application: Application) : BaseViewModel(application) {

    private val homeRepository by lazy { HomeRepository() }
    private val bookRepository by lazy { BookRepository() }
    var appResp = MutableLiveData<AppUpdateResp>()

    fun appUpdate() {
        launch({
            appResp.value = homeRepository.appUpdate()
        }, showErrorToast = false)
    }

    fun upAllBookToc() {
        execute {
            upToc(App.db.getBookDao().getAllBooks())
        }
    }

    val updateList = CopyOnWriteArraySet<Long>()
    private val bookMap = ConcurrentHashMap<Long, Book>()

    fun upToc(books: List<Book>) {
        execute {
            books.filter {
                it.origin != BookType.local && it.canUpdate()
            }.forEach {
                bookMap[it.bookId] = it
            }
            updateToc()
        }
    }

    @Synchronized
    private fun updateToc() {
        bookMap.forEach { bookEntry ->
            if (!updateList.contains(bookEntry.key)) {
                val book = bookEntry.value
                synchronized(this) {
                    updateList.add(book.bookId)
                    postEvent(EventBus.UPDATE_BOOK, book.bookId)
                }

                launch(
                    block = {
                        val bookChapters = bookRepository.getDirectory(book.bookId)
                        dealData(bookChapters, bookEntry)
                    },
                    error = {
                        it.printStackTrace()
                        synchronized(this) {
                            bookMap.remove(bookEntry.key)
                            updateList.remove(book.bookId)
                            postEvent(EventBus.UPDATE_BOOK, book.bookId)
                            upNext()
                        }
                    },
                    showErrorToast = false,
                )
                return
            }
        }
    }

    private fun dealData(chapterResp: ChapterResp, bookE: Map.Entry<Long, Book>) {
        val book = bookE.value
        Coroutine.async {
            val cList = arrayListOf<BookChapter>()
            for (chapter in chapterResp.chapterList!!) {
                val bookChapter = BookChapter(
                    bookId = chapter.bookId,
                    chapterId = chapter.chapterId,
                    chapterIndex = chapter.chapterIndex - 1,
                    chapterName = chapter.chapterName,
                    createTimeValue = chapter.createTime,
                    updateDate = "",
                    updateTimeValue = 0L,
                    chapterUrl = chapter.chapterUrl
                )
                cList.add(bookChapter)
            }
            if (cList.isNotEmpty()) {
                App.db.getChapterDao().insert(cList.toTypedArray())
                book.totalChapterNum = cList.size
                book.lastUpdateChapterDate = cList[cList.size - 1].updateDate
                App.db.getBookDao().update(book)
            } else {
                ReadBook.upMsg(context.getString(R.string.error_load_toc))
            }
        }.onFinally {
            synchronized(this) {
                bookMap.remove(bookE.key)
                updateList.remove(book.bookId)
                postEvent(EventBus.UPDATE_BOOK, book.bookId)
                upNext()
            }
        }
    }

    private fun upNext() {
        if (bookMap.size > updateList.size) {
            updateToc()
        }
    }

}