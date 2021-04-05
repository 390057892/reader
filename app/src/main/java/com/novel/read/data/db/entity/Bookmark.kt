package com.novel.read.data.db.entity

import org.litepal.crud.LitePalSupport
import java.io.Serializable

data class Bookmark(
    var time: Long = System.currentTimeMillis(),
    var bookId: Long = 0,
    var bookName: String = "",
    val bookAuthor: String = "",
    var chapterIndex: Int = 0,
    var pageIndex: Int = 0,
    var chapterName: String = "",
    var content: String = ""

) : LitePalSupport(), Serializable