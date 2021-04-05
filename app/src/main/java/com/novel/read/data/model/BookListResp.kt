package com.novel.read.data.model

import com.novel.read.utils.StringUtils

data class BookListResp(
    val authorInformation: String,
    var authorName: String?,
    val authorUserId: Long,
    val bookClass: Int,
    val bookId: Long,
    val bookName: String,
    val bookSId: String,
    val bookStatus: Int,
    val bookTypeId: Int,
    val categoryName: String?,
    val channelName: String?,
    val coverImageUrl: String,
    val createTime: Long,
    val endStatus: Int,
    val hotStatus: Int,
    val introduction: String?,
    val keyWord: String,
    val lastUpdateChapterDate: String?,
    val recommendStatus: Int,
    val validFlag: Int,
    val wordCount: Long

) {
    fun getBAuthorName(): String {
        return StringUtils.convertCC(authorName!!)
    }

    fun getBBookName(): String {
        return StringUtils.convertCC(bookName)
    }

    fun getBCategoryName(): String {
        return StringUtils.convertCC(categoryName ?: "")
    }

    fun getBChannel(): String {
        return StringUtils.convertCC(channelName!!)
    }

    fun getBIntroduction(): String {
        return StringUtils.convertCC(introduction!!)
    }

    fun getBKeyWord(): String {
        return StringUtils.convertCC(keyWord)
    }
}