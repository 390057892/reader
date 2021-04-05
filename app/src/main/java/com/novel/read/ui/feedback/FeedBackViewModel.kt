package com.novel.read.ui.feedback

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.novel.read.base.BaseViewModel
import com.novel.read.network.repository.BookRepository

class FeedBackViewModel(application: Application) : BaseViewModel(application) {

    private val bookRepository by lazy { BookRepository() }
    var success = MutableLiveData<Boolean>()

    fun feedback(content: String) {
        launch(block = {
            bookRepository.feedback(content)
            success.postValue(true)
        })
    }
}