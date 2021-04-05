package com.novel.read.data.db.entity

data class ChapterDetailEntity(
    val chapterContent: String,
    val chapterIndex: Int,
    val chapterName: String,
    val isRecommend: String,
    val isVIP: String,
    val wordCount: Int
)