package com.novel.read.ui.main.mail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.novel.read.base.BaseViewModel
import com.novel.read.constant.AppConst
import com.novel.read.data.model.HomeResp
import com.novel.read.network.repository.HomeRepository

class MailViewModel(application: Application) : BaseViewModel(application) {
    private val homeRepository by lazy { HomeRepository() }

    var homeResp = MutableLiveData<HomeResp>()
    val refreshStatus = MutableLiveData<Int>()

    fun getAll() {
        refreshStatus.value = AppConst.loading
        launch(block = {
            homeResp.value = homeRepository.getHomeBook(AppConst.home)
            refreshStatus.value = AppConst.complete
        }, error = {
            refreshStatus.value = AppConst.error
        }, showErrorToast = false)
    }

}