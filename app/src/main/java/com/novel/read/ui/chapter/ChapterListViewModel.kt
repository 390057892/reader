package com.novel.read.ui.chapter

import android.app.Application
import com.novel.read.App
import com.novel.read.base.BaseViewModel
import com.novel.read.data.db.entity.Book

class ChapterListViewModel(application: Application) : BaseViewModel(application) {
    var bookId: String = ""
    var book: Book? = null
    var chapterCallBack: ChapterListCallBack? = null
    var bookMarkCallBack: BookmarkCallBack? = null

    fun initBook(bookId: String, success: () -> Unit) {
        this.bookId = bookId
        execute {
            book = App.db.getBookDao().getBook(bookId)
        }.onSuccess {
            success.invoke()
        }
    }

    fun startChapterListSearch(newText: String?) {
        chapterCallBack?.startChapterListSearch(newText)
    }

    fun startBookmarkSearch(newText: String?) {
        bookMarkCallBack?.startBookmarkSearch(newText)
    }

    interface ChapterListCallBack {
        fun startChapterListSearch(newText: String?)
    }

    interface BookmarkCallBack {
        fun startBookmarkSearch(newText: String?)
    }
}