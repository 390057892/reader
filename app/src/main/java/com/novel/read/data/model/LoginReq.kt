package com.novel.read.data.model

data class LoginReq(
    val email: String,
    val headImageUrl: String,
    val idToken: String,
    val ip: String,
    var isEmailVerified: Int,
    val nickName: String,
    val os: String = "Android",
    val phone: String,
    val provider: String
){
    fun setIsEmail(boolean: Boolean){
        isEmailVerified = if (boolean){
            0
        }else{
            1
        }
    }
}