package com.novel.read.data.read

import kotlin.math.min

data class TextChapter(
    val position: Int,
    val title: String,
    val chapterId: Int,
    val pages: List<TextPage>,
    val pageLines: List<Int>,
    val pageLengths: List<Int>,
    val chaptersSize: Int
) {
    fun page(index: Int): TextPage? {
        return pages.getOrNull(index)
    }

    val lastPage: TextPage? get() = pages.lastOrNull()

    val lastIndex: Int get() = pages.lastIndex

    val pageSize: Int get() = pages.size

    fun isLastIndex(index: Int): Boolean {
        return index >= pages.size - 1
    }

    fun getReadLength(pageIndex: Int): Int {
        var length = 0
        val maxIndex = min(pageIndex, pages.size)
        for (index in 0 until maxIndex) {
            length += pageLengths[index]
        }
        return length
    }

    fun getUnRead(pageIndex: Int): String {
        val stringBuilder = StringBuilder()
        if (pages.isNotEmpty()) {
            for (index in pageIndex..pages.lastIndex) {
                stringBuilder.append(pages[index].text)
            }
        }
        return stringBuilder.toString()
    }

    fun getContent(): String {
        val stringBuilder = StringBuilder()
        pages.forEach {
            stringBuilder.append(it.text)
        }
        return stringBuilder.toString()
    }
}