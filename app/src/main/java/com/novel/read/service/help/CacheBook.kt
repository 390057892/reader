package com.novel.read.service.help

import android.content.Context
import android.content.Intent
import com.novel.read.App
import com.novel.read.R
import com.novel.read.constant.IntentAction
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.help.BookHelp
import com.novel.read.network.repository.BookRepository
import com.novel.read.service.CacheBookService
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

object CacheBook {
    val logs = arrayListOf<String>()
    private val downloadMap = ConcurrentHashMap<Long, CopyOnWriteArraySet<Int>>()
    private val bookRepository by lazy { BookRepository() }
    fun addLog(log: String?) {
        log ?: return
        synchronized(this) {
            if (logs.size > 1000) {
                logs.removeAt(0)
            }
            logs.add(log)
        }
    }

    fun start(context: Context, bookId: Long, start: Int, end: Int) {
        Intent(context, CacheBookService::class.java).let {
            it.action = IntentAction.start
            it.putExtra("bookId", bookId)
            it.putExtra("start", start)
            it.putExtra("end", end)
            context.startService(it)
        }
    }

    fun remove(context: Context, bookUrl: String) {
        Intent(context, CacheBookService::class.java).let {
            it.action = IntentAction.remove
            it.putExtra("bookUrl", bookUrl)
            context.startService(it)
        }
    }

    fun stop(context: Context) {
        Intent(context, CacheBookService::class.java).let {
            it.action = IntentAction.stop
            context.startService(it)
        }
    }

    fun downloadCount(): Int {
        var count = 0
        downloadMap.forEach {
            count += it.value.size
        }
        return count
    }

    fun download(
        scope: CoroutineScope,
        book: Book,
        chapter: BookChapter,
        resetPageOffset: Boolean = false
    ) {
        if (downloadMap[book.bookId]?.contains(chapter.chapterIndex) == true) {
            return
        }
        if (downloadMap[book.bookId] == null) {
            downloadMap[book.bookId] = CopyOnWriteArraySet()
        }
        downloadMap[book.bookId]?.add(chapter.chapterIndex)

        bookRepository.getContents(scope,book,chapter).onSuccess { bookContent->
            val content = bookContent.chapter.chapterContent

            if (content.isNotBlank()) {
                BookHelp.saveContent(book, chapter, content)
            }
            if (ReadBook.book?.bookId == book.bookId) {
                ReadBook.contentLoadFinish(
                    book,
                    chapter,
                    content.ifBlank { App.INSTANCE.getString(R.string.content_empty) },
                    resetPageOffset = resetPageOffset
                )
            }
        }.onError {
            if (ReadBook.book?.bookId == book.bookId) {
                ReadBook.contentLoadFinish(
                    book,
                    chapter,
                    it.message!!,
                    resetPageOffset = resetPageOffset
                )
            }
        }.onFinally {
            downloadMap[book.bookId]?.remove(chapter.chapterIndex)
            if (downloadMap[book.bookId].isNullOrEmpty()) {
                downloadMap.remove(book.bookId)
            }
            ReadBook.removeLoading(chapter.chapterIndex)
        }
    }

}