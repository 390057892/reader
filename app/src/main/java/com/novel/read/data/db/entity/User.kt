package com.novel.read.data.db.entity

import org.litepal.crud.LitePalSupport
import java.io.Serializable

data class User(
    val createTime: Long,
    val email: String,
    val headImageUrl: String,
    val idToken: String,
    val introduction: String?,
    val ip: String,
    val isEmailVerified: Int,
    val lastLoginTime: Long?,
    val nickName: String?,
    val oldNickName: String?,
    val os: String,
    val password: String,
    val phone: String,
    val provider: String,
    val sex: Int,
    val updateTime: Long?,
    val userId: Long,
    val userName: String,
    val validFlag: Int,
    val vipEndTime: Long?,
    val vipStartTime: Long?,
    val vipStatus: Int
) : LitePalSupport(), Serializable