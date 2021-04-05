package com.novel.read.ui.read.page

import com.novel.read.data.read.TextChapter
import com.novel.read.service.help.ReadBook

interface DataSource {

    val pageIndex: Int get() = ReadBook.durChapterPos()

    val currentChapter: TextChapter?

    val nextChapter: TextChapter?

    val prevChapter: TextChapter?

    fun hasNextChapter(): Boolean

    fun hasPrevChapter(): Boolean

    fun upContent(relativePosition: Int = 0, resetPageOffset: Boolean = true)
}