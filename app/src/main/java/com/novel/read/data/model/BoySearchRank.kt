package com.novel.read.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.novel.read.constant.LayoutType
import com.novel.read.utils.StringUtils

data class BoySearchRank(
    val authorInformation: String,
    val authorName: String,
    val authorUserId: Long,
    val bookChannelId: Long,
    val bookClass: Int,
    val bookId: Long,
    val bookName: String,
    val bookSId: String,
    val bookStatus: Int,
    val bookTypeId: Long,
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
    fun getBAuthorName(): String {
        return StringUtils.convertCC(authorName)
    }

    fun getBBookName(): String {
        return StringUtils.convertCC(bookName)
    }

    fun getBCategoryName(): String {
        return StringUtils.convertCC(categoryName)
    }
}

data class BoySearchRankEntity(val boySearchRanks:List<BoySearchRank>):MultiItemEntity{
    override val itemType: Int
        get() = LayoutType.BOY_SEARCH
}