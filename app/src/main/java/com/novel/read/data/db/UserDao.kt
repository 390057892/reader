package com.novel.read.data.db

import com.novel.read.data.db.entity.User
import org.litepal.LitePal
import org.litepal.extension.deleteAll

class UserDao {

    fun saveUser(user: User) = user.saveOrUpdate("userId=?", user.userId.toString())

    fun getUser(): User? =
        LitePal.findFirst(User::class.java)

    fun deleteUser() = LitePal.deleteAll<User>()

}