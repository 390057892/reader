package com.novel.read.data.db.entity

import org.litepal.crud.LitePalSupport

data class HttpTTS(
    val id: Long = System.currentTimeMillis(),
    var name: String = "",
    var url: String = ""
) : LitePalSupport()