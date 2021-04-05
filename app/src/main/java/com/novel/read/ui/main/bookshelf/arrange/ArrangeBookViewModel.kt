package com.novel.read.ui.main.bookshelf.arrange

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.novel.read.App
import com.novel.read.base.BaseViewModel
import com.novel.read.constant.EventBus
import com.novel.read.data.db.entity.Book
import com.novel.read.utils.ext.postEvent

class ArrangeBookViewModel(application: Application) : BaseViewModel(application) {
    var booksLiveData = MutableLiveData<List<Book>>()

    fun deleteBook(book: Book) {
        App.db.getBookDao().delete(book)
        booksLiveData.value = App.db.getBookDao().getAllBooks()
        postEvent(EventBus.UP_BOOK, 0L)
    }

}