package com.novel.read.ui.channel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.novel.read.base.BaseViewModel
import com.novel.read.constant.AppConst
import com.novel.read.data.model.ChannelResp
import com.novel.read.network.repository.BookRepository

class ChannelViewModel(application: Application) : BaseViewModel(application) {

    private val bookRepository by lazy { BookRepository() }
    var channelResp = MutableLiveData<ChannelResp>()
    val refreshStatus = MutableLiveData<Int>()
    fun getChannel(){
        refreshStatus.value = AppConst.loading
        launch({
            channelResp.value = bookRepository.getChannelList()
            refreshStatus.value = AppConst.complete
        }, {
            refreshStatus.value = AppConst.error
        })
    }
}