package com.novel.read.data.db.entity

import org.litepal.crud.LitePalSupport

import java.io.Serializable

/**
 * Created by zlj
 */
class ReadRecord(
    var androidId: String = "",
    var bookName: String = "",
    var bookId: Long = 0L,
    var readTime: Long = 0L
) : LitePalSupport(), Serializable
