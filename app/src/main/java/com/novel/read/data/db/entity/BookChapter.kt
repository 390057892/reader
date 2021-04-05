package com.novel.read.data.db.entity

import org.litepal.crud.LitePalSupport

data class BookChapter(
//    val bookId: Int,
//    val chapterList: List<Chapter>,
//    val volumeId: Int,
//    val volumeName: String

    val chapterId: Long,
    val bookId: Long,
    val chapterIndex: Int,
    var chapterName: String,
    val createTimeValue: Long,
    val updateDate: String,
    val updateTimeValue: Long,
    val chapterUrl: String?,
) : LitePalSupport()