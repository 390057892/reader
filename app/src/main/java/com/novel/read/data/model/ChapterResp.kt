package com.novel.read.data.model

data class ChapterResp(
    val chapterList: List<Chapter>?,
    val count: Int
)

data class Chapter(
    val bookId: Long,
    val chapterId: Long,
    val chapterIndex: Int,
    val chapterName: String,
    val chapterUrl: String?,
    val createTime: Long,
    val validFlag: Int
)