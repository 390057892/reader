package com.novel.read.data.model

data class BookInfoResp(
    val book: BookResp
)

data class BookResp(
    val authorInformation: Any,
    val authorName: String,
    val authorUserId: Long,
    val bookClass: Any,
    val bookId: Long,
    val bookName: String,
    val bookSId: String,
    val bookStatus: Any,
    val bookTypeId: Int,
    val categoryName: String?,
    val channelName: Any?,
    val coverImageUrl: String,
    val createTime: Long,
    val endStatus: Int,
    val extBookId: Any,
    val hits: Any,
    val hotStatus: Any,
    val introduction: String,
    val keyWord: String,
    val lastUpdateChapterDate: String,
    val rankNumber: Any,
    val rankType: Any,
    val recommendStatus: Any,
    val validFlag: Int,
    val wordCount: Long
)