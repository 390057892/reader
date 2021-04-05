package com.novel.read.data.model

import com.novel.read.data.db.entity.User

data class UserResp(
    val user: User
)
//
//data class User(
//    val createTime: Long,
//    val email: String,
//    val headImageUrl: String,
//    val idToken: String,
//    val introduction: Any,
//    val ip: String,
//    val isEmailVerified: Int,
//    val lastLoginTime: Any,
//    val nickName: String,
//    val oldNickName: Any,
//    val os: String,
//    val password: String,
//    val phone: String,
//    val provider: String,
//    val sex: Any,
//    val updateTime: Any,
//    val userId: Long,
//    val userName: String,
//    val validFlag: Int,
//    val vipEndTime: Any,
//    val vipStartTime: Any,
//    val vipStatus: Int
//)