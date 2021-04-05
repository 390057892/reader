package com.novel.read.data.model

data class HomeBanner(
    val bannerId: Int,
    val bookChannelId: Long,
    val bookId: Int,
    val createTime: String,
    val imgUrl: String,
    val isDel: Int,
    val type: Int
)