package com.novel.read.ui.read

import android.app.Application
import android.content.Intent
import android.util.Log
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.BaseViewModel
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.help.BookHelp
import com.novel.read.help.IntentDataHelp
import com.novel.read.network.repository.BookRepository
import com.novel.read.service.BaseReadAloudService
import com.novel.read.service.help.ReadAloud
import com.novel.read.service.help.ReadBook

class ReadBookViewModel(application: Application) : BaseViewModel(application) {
    private val bookRepository by lazy { BookRepository() }
    var isInitFinish = false
    var searchContentQuery = ""

    fun initData(intent: Intent) {
        execute {
            ReadBook.inBookshelf = intent.getBooleanExtra("inBookshelf", true)
            IntentDataHelp.getData<Book>(intent.getStringExtra("key"))?.let {
                initBook(it)
            } ?: intent.getStringExtra("bookId")?.let {
                App.db.getBookDao().getBook(it)?.let { book ->
                    initBook(book)
                }
            } ?: App.db.getBookDao().lastReadBook().let {
                initBook(it)
            }
        }.onFinally {
            if (ReadBook.inBookshelf) {
                ReadBook.saveRead()
            }
        }
    }

    private fun initBook(book: Book) {
        if (ReadBook.book?.bookId != book.bookId) {
            ReadBook.resetData(book)
            isInitFinish = true
            ReadBook.chapterSize = App.db.getChapterDao().getChapterCount(book.bookId)
            if (ReadBook.chapterSize == 0 || !ReadBook.inBookshelf) {
                loadChapterList(book)
            } else {
                if (ReadBook.durChapterIndex > ReadBook.chapterSize - 1) {
                    ReadBook.durChapterIndex = ReadBook.chapterSize - 1
                }
                ReadBook.loadContent(resetPageOffset = true)
            }
        } else {
            ReadBook.book = book
            if (ReadBook.durChapterIndex != book.durChapterIndex) {
                ReadBook.durChapterIndex = book.durChapterIndex
                ReadBook.durPageIndex = book.durChapterPos
                ReadBook.prevTextChapter = null
                ReadBook.curTextChapter = null
                ReadBook.nextTextChapter = null
            }
            ReadBook.titleDate.postValue(book.bookName)
            isInitFinish = true
            ReadBook.chapterSize = App.db.getChapterDao().getChapterCount(book.bookId)
            if (ReadBook.chapterSize == 0 || !ReadBook.inBookshelf) {
                loadChapterList(book)
            } else {
                if (ReadBook.curTextChapter != null) {
                    ReadBook.callBack?.upContent(resetPageOffset = false)
                } else {
                    ReadBook.loadContent(resetPageOffset = true)
                }
            }
        }
    }

    fun loadChapterList(
        book: Book,
        changeDruChapterIndex: ((chapters: List<BookChapter>) -> Unit)? = null
    ) {
        Log.e("loadChapterList", "loadChapterList: ")
        if (book.isLocalBook()) {
//            execute {
//                LocalBook.getChapterList(book).let {
//                    App.db.getChapterDao().delByBook(book.bookUrl)
//                    App.db.getChapterDao().insert(*it.toTypedArray())
//                    App.db.getBookDao().update(book)
//                    ReadBook.chapterSize = it.size
//                    if (it.isEmpty()) {
//                        ReadBook.upMsg(context.getString(R.string.error_load_toc))
//                    } else {
//                        ReadBook.upMsg(null)
//                        ReadBook.loadContent(resetPageOffset = true)
//                    }
//                }
//            }.onError {
//                ReadBook.upMsg("LoadTocError:${it.localizedMessage}")
//            }
        } else {
            launch(
                block = {
                    val bookChapters = bookRepository.getDirectory(book.bookId).chapterList

                    val cList = arrayListOf<BookChapter>()
                    for (chapter in bookChapters!!) {
                        val bookChapter = BookChapter(
                            bookId = chapter.bookId,
                            chapterId = chapter.chapterId,
                            chapterIndex = chapter.chapterIndex - 1,
                            chapterName =  chapter.chapterName,
                            createTimeValue = chapter.createTime,
                            updateDate = "",
                            updateTimeValue = 0L,
                            chapterUrl = chapter.chapterUrl
                        )
                        cList.add(bookChapter)
                    }
                    if (cList.isNotEmpty()) {
                        if (changeDruChapterIndex == null) {
                            App.db.getChapterDao().insert(cList.toTypedArray())
                            book.totalChapterNum = cList.size
                            App.db.getBookDao().update(book)
                            ReadBook.chapterSize = cList.size
                            ReadBook.upMsg(null)
                            ReadBook.loadContent(resetPageOffset = true)
                        } else {
                            changeDruChapterIndex(cList)
                        }
                    } else {
                        ReadBook.upMsg(context.getString(R.string.error_load_toc))
                    }
                },error = {
                    ReadBook.upMsg(context.getString(R.string.error_load_toc))
                }
            )
        }
    }


    fun openChapter(index: Int, pageIndex: Int = 0) {
        ReadBook.prevTextChapter = null
        ReadBook.curTextChapter = null
        ReadBook.nextTextChapter = null
        ReadBook.callBack?.upContent()
        if (index != ReadBook.durChapterIndex) {
            ReadBook.durChapterIndex = index
            ReadBook.durPageIndex = pageIndex
        }
        ReadBook.saveRead()
        ReadBook.loadContent(resetPageOffset = true)
    }

    fun removeFromBookshelf(success: (() -> Unit)?) {
        execute {
            ReadBook.book?.delete()
        }.onSuccess {
            success?.invoke()
        }
    }


    fun refreshContent(book: Book) {
        execute {
            App.db.getChapterDao().getChapter(book.bookId, ReadBook.durChapterIndex)
                ?.let { chapter ->
                    BookHelp.delContent(book, chapter)
                    ReadBook.loadContent(ReadBook.durChapterIndex, resetPageOffset = false)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (BaseReadAloudService.pause) {
            ReadAloud.stop(context)
        }
    }


}