package com.novel.read.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.novel.read.constant.LayoutType
import com.novel.read.utils.StringUtils

data class HotRank(
    val authorInformation: String,
    val authorName: String,
    val authorUserId: Long,
    val bookClass: Int,
    val bookId: Long,
    val bookName: String,
    val bookSId: String,
    val bookStatus: Int,
    val bookTypeId: Int,
    val categoryName: String,
    val channelName: String,
    val coverImageUrl: String,
    val createTime: Long,
    val endStatus: Int,
    val extBookId: Int,
    val hits: String,
    val hotStatus: Int,
    val introduction: String,
    val keyWord: String,
    val lastUpdateChapterDate: String,
    val rankNumber: String,
    val rankType: String,
    val recommendStatus: Int,
    val validFlag: Int
){
    fun getBName(): String {
        return StringUtils.convertCC(bookName)
    }

    fun getBAuthor(): String {
        return StringUtils.convertCC(authorName)
    }
}

data class HotEntity(val hotRanks: List<HotRank>) : MultiItemEntity {
    override val itemType: Int
        get() = LayoutType.HOT
}