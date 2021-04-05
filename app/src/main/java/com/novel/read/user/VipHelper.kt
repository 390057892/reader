package com.novel.read.user

import com.novel.read.App
import com.novel.read.data.db.entity.User

object VipHelper {

    var user: User? = null

    fun notifyUser() {
        user = App.db.getUserDao().getUser()
    }

    fun showAd(): Boolean {
        return false
//        return if (user == null) {
//            true
//        } else {
//            !isVip(user)
//        }
    }

    private fun isVip(user: User?): Boolean {
        val currentTime = System.currentTimeMillis()
        return user?.vipStatus == 1 && currentTime > user.vipStartTime!! && currentTime < user.vipEndTime!!
    }

}