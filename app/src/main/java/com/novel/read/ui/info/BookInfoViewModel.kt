package com.novel.read.ui.info

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.novel.read.App
import com.novel.read.base.BaseViewModel
import com.novel.read.constant.AppConst
import com.novel.read.constant.IntentAction
import com.novel.read.data.db.entity.Book
import com.novel.read.data.model.BookListResp
import com.novel.read.data.model.BookResp
import com.novel.read.network.repository.BookRepository
import com.novel.read.service.help.ReadBook

class BookInfoViewModel(application: Application) : BaseViewModel(application) {

    var bookResp = MutableLiveData<BookResp>()
    val bookData = MutableLiveData<Book>()
    var status = MutableLiveData<Int>()
    var inBookshelf = false
    private val bookRepository by lazy { BookRepository() }
    var bookListResp = MutableLiveData<List<BookListResp>>()
    private lateinit var mList: MutableList<BookListResp>
    private var bookTypeId: Int = 0

    private var durChapterTime: Long = 0
    private var durChapterIndex: Int = 0
    private var durChapterPos: Int = 0
    private var durChapterTitle: String? = ""
    private var totalChapterNum: Int = 0

    fun initData(intent: Intent) {

        val bookId = intent.getLongExtra(IntentAction.bookId,0L)

        App.db.getBookDao().getBook(bookId.toString())?.let { book ->
            inBookshelf = true
            durChapterTime = book.durChapterTime
            durChapterIndex = book.durChapterIndex
            durChapterPos = book.durChapterPos
            totalChapterNum = book.totalChapterNum
            durChapterTitle = book.durChapterTitle
        }
        launch(
            block = {
                status.postValue(AppConst.loading)
                val data = bookRepository.getBookInfo(bookId).book
                bookResp.postValue(data)
                val book = Book(
                    authorPenname = data.authorName,
                    bookId = data.bookId,
                    bookName = data.bookName,
                    bookStatus = "01",
                    categoryName = data.categoryName,
                    channelName = data.categoryName,
                    cName = "轻小说",
                    coverImageUrl = data.coverImageUrl,
                    introduction = data.introduction,
                    keyWord = data.keyWord,
                    lastUpdateChapterDate = data.lastUpdateChapterDate,
                    status = 0,
                    wordCount = data.wordCount,
                    bookTypeId = data.bookTypeId
                )
                book.durChapterTime = durChapterTime
                book.durChapterIndex = durChapterIndex
                book.durChapterPos = durChapterPos
                book.durChapterTitle = durChapterTitle
                book.totalChapterNum = totalChapterNum
                setBookData(book)
                status.postValue(AppConst.complete)
            }, error = {
                status.postValue(AppConst.error)
            }, showErrorToast = false
        )

    }

    private fun setBookData(book: Book) {
        bookData.postValue(book)
        if (inBookshelf) {
            App.db.getBookDao().update(book)
        }
    }

    fun getRecommend(intent: Intent) {
        launch({
            bookTypeId = intent.getIntExtra(IntentAction.bookTypeId,0)
            val data = bookRepository.getSimilarRecommend(bookTypeId)
            mList = data.recommendBookList
            bookListResp.value = mList
        }, {
        }, showErrorToast = false)
    }

    fun delBook(success: (() -> Unit)? = null) {
        execute {
            bookData.value?.let {
                App.db.getBookDao().delete(it)
                inBookshelf = false
            }
        }.onSuccess {
            success?.invoke()
        }
    }

    fun addToBookshelf(success: (() -> Unit)?) {
        execute {
            bookData.value?.let { book ->
                book.bookTypeId = bookTypeId
                App.db.getBookDao().getBook(book.bookId.toString())?.let {
                    book.durChapterPos = it.durChapterPos
                    book.durChapterTitle = it.durChapterTitle
                }
                App.db.getBookDao().insert(book)
            }

            inBookshelf = true
        }.onSuccess {
            success?.invoke()
        }
    }

    fun saveBook(success: (() -> Unit)? = null) {
        execute {
            bookData.value?.let { book ->
                App.db.getBookDao().getBook(book.bookId.toString())?.let {
                    book.durChapterPos = it.durChapterPos
                    book.durChapterTitle = it.durChapterTitle
                }
                App.db.getBookDao().insert(book)
                if (ReadBook.book?.bookName == book.bookName && ReadBook.book?.authorPenname == book.authorPenname) {
                    ReadBook.book = book
                }
            }
        }.onSuccess {
            success?.invoke()
        }
    }

}