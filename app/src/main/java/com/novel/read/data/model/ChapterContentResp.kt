package com.novel.read.data.model

data class ChapterContentResp(
    val chapter: ChapterContent
)

data class ChapterContent(
    val bookId: Long,
    val chapterContent: String,
    val chapterId: Long,
    val chapterIndex: Int,
    val chapterName: String,
    val chapterUrl: Any,
    val createTime: Long,
    val validFlag: Int
)