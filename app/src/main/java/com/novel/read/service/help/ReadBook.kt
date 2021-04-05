package com.novel.read.service.help

import androidx.lifecycle.MutableLiveData
import com.hankcs.hanlp.HanLP
import com.novel.read.App
import com.novel.read.constant.AppConst
import com.novel.read.constant.BookType
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.data.db.entity.ReadRecord
import com.novel.read.data.read.TextChapter
import com.novel.read.data.read.TextPage
import com.novel.read.help.AppConfig
import com.novel.read.help.BookHelp
import com.novel.read.help.IntentDataHelp
import com.novel.read.help.ReadBookConfig
import com.novel.read.service.BaseReadAloudService
import com.novel.read.help.coroutine.Coroutine
import io.legado.app.ui.book.read.page.provider.ChapterProvider
import io.legado.app.ui.book.read.page.provider.ImageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.toast


object ReadBook {
    var titleDate = MutableLiveData<String>()
    var book: Book? = null
    var inBookshelf = false
    var chapterSize = 0
    var durChapterIndex = 0
    var durPageIndex = 0
    var isLocalBook = true
    var callBack: CallBack? = null
    var prevTextChapter: TextChapter? = null
    var curTextChapter: TextChapter? = null
    var nextTextChapter: TextChapter? = null
    var msg: String? = null
    private val loadingChapters = arrayListOf<Int>()
    private val readRecord = ReadRecord()
    var readStartTime: Long = System.currentTimeMillis()

    fun resetData(book: Book) {
        this.book = book
        readRecord.bookName = book.bookName
        readRecord.readTime = App.db.getReadRecordDao().getReadTime(book.bookName) ?: 0
        durChapterIndex = book.durChapterIndex
        durPageIndex = book.durChapterPos
        isLocalBook = book.origin == BookType.local
        chapterSize = 0
        prevTextChapter = null
        curTextChapter = null
        nextTextChapter = null
        titleDate.postValue(book.bookName)
        ImageProvider.clearAllCache()
        synchronized(this) {
            loadingChapters.clear()
        }
    }


    fun upReadStartTime() {
        Coroutine.async {
            readRecord.readTime = readRecord.readTime + System.currentTimeMillis() - readStartTime
            readStartTime = System.currentTimeMillis()
            App.db.getReadRecordDao().insert(readRecord)
        }
    }

    fun upMsg(msg: String?) {
        if (this.msg != msg) {
            this.msg = msg
            callBack?.upContent()
        }
    }

    fun moveToNextPage() {
        durPageIndex++
        callBack?.upContent()
        saveRead()
    }

    fun moveToNextChapter(upContent: Boolean): Boolean {
        if (durChapterIndex < chapterSize - 1) {
            durPageIndex = 0
            durChapterIndex++
            prevTextChapter = curTextChapter
            curTextChapter = nextTextChapter
            nextTextChapter = null
            book?.let {
                if (curTextChapter == null) {
                    loadContent(durChapterIndex, upContent, false)
                } else if (upContent) {
                    callBack?.upContent()
                }
                loadContent(durChapterIndex.plus(1), upContent, false)
                GlobalScope.launch(Dispatchers.IO) {
                    for (i in 2..10) {
                        delay(100)
                        download(durChapterIndex + i)
                    }
                }
            }
            saveRead()
            callBack?.upView()
            curPageChanged()
            return true
        } else {
            return false
        }
    }

    fun moveToPrevChapter(upContent: Boolean, toLast: Boolean = true): Boolean {
        if (durChapterIndex > 0) {
            durPageIndex = if (toLast) prevTextChapter?.lastIndex ?: 0 else 0
            durChapterIndex--
            nextTextChapter = curTextChapter
            curTextChapter = prevTextChapter
            prevTextChapter = null
            book?.let {
                if (curTextChapter == null) {
                    loadContent(durChapterIndex, upContent, false)
                } else if (upContent) {
                    callBack?.upContent()
                }
                loadContent(durChapterIndex.minus(1), upContent, false)
                GlobalScope.launch(Dispatchers.IO) {
                    for (i in -5..-2) {
                        delay(100)
                        download(durChapterIndex + i)
                    }
                }
            }
            saveRead()
            callBack?.upView()
            curPageChanged()
            return true
        } else {
            return false
        }
    }

    fun skipToPage(page: Int) {
        durPageIndex = page
        callBack?.upContent()
        curPageChanged()
        saveRead()
    }

    fun setPageIndex(pageIndex: Int) {
        durPageIndex = pageIndex
        saveRead()
        curPageChanged()
    }

    private fun curPageChanged() {
        callBack?.pageChanged()
        if (BaseReadAloudService.isRun) {
            readAloud(!BaseReadAloudService.pause)
        }
        upReadStartTime()
    }

    /**
     * 朗读
     */
    fun readAloud(play: Boolean = true) {
        val book = book
        val textChapter = curTextChapter
        if (book != null && textChapter != null) {
            val key = IntentDataHelp.putData(textChapter)
            ReadAloud.play(
                App.INSTANCE,
                book.bookName,
                textChapter.title,
                durPageIndex,
                key,
                play
            )
        }
    }

    fun durChapterPos(): Int {
        curTextChapter?.let {
            if (durPageIndex < it.pageSize) {
                return durPageIndex
            }
            return it.pageSize - 1
        }
        return durPageIndex
    }

    /**
     * chapterOnDur: 0为当前页,1为下一页,-1为上一页
     */
    fun textChapter(chapterOnDur: Int = 0): TextChapter? {
        return when (chapterOnDur) {
            0 -> curTextChapter
            1 -> nextTextChapter
            -1 -> prevTextChapter
            else -> null
        }
    }

    /**
     * 加载章节内容
     */
    fun loadContent(resetPageOffset: Boolean) {
        loadContent(durChapterIndex, resetPageOffset = resetPageOffset)
        loadContent(durChapterIndex + 1, resetPageOffset = resetPageOffset)
        loadContent(durChapterIndex - 1, resetPageOffset = resetPageOffset)
    }

    fun loadContent(index: Int, upContent: Boolean = true, resetPageOffset: Boolean) {
        book?.let { book ->
            if (addLoading(index)) {
                Coroutine.async {
                    App.db.getChapterDao().getChapter(book.bookId, index)?.let { chapter ->
                        BookHelp.getContent(book, chapter)?.let {
                            contentLoadFinish(book, chapter, it, upContent, resetPageOffset)
                            removeLoading(chapter.chapterIndex)
                        } ?: download(chapter, resetPageOffset = resetPageOffset)
                    } ?: removeLoading(index)
                }.onError {
                    removeLoading(index)
                }
            }
        }
    }

    private fun download(index: Int) {
        book?.let { book ->
            if (book.isLocalBook()) return
            if (addLoading(index)) {
                Coroutine.async {
                    App.db.getChapterDao().getChapter(book.bookId, index)?.let { chapter ->
                        if (BookHelp.hasContent(book, chapter)) {
                            removeLoading(chapter.chapterIndex)
                        } else {
                            download(chapter, false)
                        }
                    } ?: removeLoading(index)
                }.onError {
                    removeLoading(index)
                }
            }
        }
    }

    private fun download(chapter: BookChapter, resetPageOffset: Boolean) {
        val book = book
        if (book != null) {
            CacheBook.download(Coroutine.DEFAULT,book, chapter)
        } else if (book != null) {
            contentLoadFinish(
                book,
                chapter,
                "没有书源",
                resetPageOffset = resetPageOffset
            )
            removeLoading(chapter.chapterIndex)
        } else {
            removeLoading(chapter.chapterIndex)
        }
    }

    private fun addLoading(index: Int): Boolean {
        synchronized(this) {
            if (loadingChapters.contains(index)) return false
            loadingChapters.add(index)
            return true
        }
    }

    fun removeLoading(index: Int) {
        synchronized(this) {
            loadingChapters.remove(index)
        }
    }

    fun searchResultPositions(
        pages: List<TextPage>,
        indexWithinChapter: Int,
        query: String
    ): Array<Int> {
        // calculate search result's pageIndex
        var content = ""
        pages.map {
            content += it.text
        }
        var count = 1
        var index = content.indexOf(query)
        while (count != indexWithinChapter) {
            index = content.indexOf(query, index + 1)
            count += 1
        }
        val contentPosition = index
        var pageIndex = 0
        var length = pages[pageIndex].text.length
        while (length < contentPosition) {
            pageIndex += 1
            if (pageIndex > pages.size) {
                pageIndex = pages.size
                break
            }
            length += pages[pageIndex].text.length
        }

        // calculate search result's lineIndex
        val currentPage = pages[pageIndex]
        var lineIndex = 0
        length = length - currentPage.text.length + currentPage.textLines[lineIndex].text.length
        while (length < contentPosition) {
            lineIndex += 1
            if (lineIndex > currentPage.textLines.size) {
                lineIndex = currentPage.textLines.size
                break
            }
            length += currentPage.textLines[lineIndex].text.length
        }

        // charIndex
        val currentLine = currentPage.textLines[lineIndex]
        length -= currentLine.text.length
        val charIndex = contentPosition - length
        var addLine = 0
        var charIndex2 = 0
        // change line
        if ((charIndex + query.length) > currentLine.text.length) {
            addLine = 1
            charIndex2 = charIndex + query.length - currentLine.text.length - 1
        }
        // changePage
        if ((lineIndex + addLine + 1) > currentPage.textLines.size) {
            addLine = -1
            charIndex2 = charIndex + query.length - currentLine.text.length - 1
        }
        return arrayOf(pageIndex, lineIndex, charIndex, addLine, charIndex2)
    }

    /**
     * 内容加载完成
     */
    fun contentLoadFinish(
        book: Book,
        chapter: BookChapter,
        content: String,
        upContent: Boolean = true,
        resetPageOffset: Boolean
    ) {
        Coroutine.async {
            if (chapter.chapterIndex in durChapterIndex - 1..durChapterIndex + 1) {
                chapter.chapterName = when (AppConfig.chineseConverterType) {
                    1 -> HanLP.convertToSimplifiedChinese(chapter.chapterName)
                    2 -> HanLP.convertToTraditionalChinese(chapter.chapterName)
                    else -> chapter.chapterName
                }
                val contents = BookHelp.disposeContent(
                    book,
                    chapter.chapterName,
                    content
                )
                when (chapter.chapterIndex) {
                    durChapterIndex -> {
                        curTextChapter =
                            ChapterProvider.getTextChapter(
                                book,
                                chapter,
                                contents,
                                chapterSize,
                                ""
                            )
                        if (upContent) callBack?.upContent(resetPageOffset = resetPageOffset)
                        callBack?.upView()
                        curPageChanged()
                        callBack?.contentLoadFinish()
                        ImageProvider.clearOut(durChapterIndex)
                    }
                    durChapterIndex - 1 -> {
                        prevTextChapter =
                            ChapterProvider.getTextChapter(
                                book,
                                chapter,
                                contents,
                                chapterSize,
                                ""
                            )
                        if (upContent) callBack?.upContent(-1, resetPageOffset)
                    }
                    durChapterIndex + 1 -> {
                        nextTextChapter =
                            ChapterProvider.getTextChapter(
                                book,
                                chapter,
                                contents,
                                chapterSize,
                                ""
                            )
                        if (upContent) callBack?.upContent(1, resetPageOffset)
                    }
                }
            }
        }.onError {
            it.printStackTrace()
            App.INSTANCE.toast("ChapterProvider ERROR:\n${it.getStackTraceString()}")
        }
    }

    fun pageAnim(): Int {
//        book?.let {
//            return if (it.getPageAnim() < 0)
//                ReadBookConfig.pageAnim
//            else
//                it.getPageAnim()
//        }
        return ReadBookConfig.pageAnim
    }

    fun saveRead() {
        Coroutine.async {
            book?.let { book ->
                book.durChapterTime = System.currentTimeMillis()
                book.durChapterIndex = durChapterIndex
                book.durChapterPos = durPageIndex
                App.db.getChapterDao().getChapter(book.bookId, durChapterIndex)?.let {
                    book.durChapterTitle = it.chapterName
                }
                App.db.getBookDao().update(book)
            }
        }
    }

    interface CallBack {
        fun loadChapterList(book: Book)
        fun upContent(relativePosition: Int = 0, resetPageOffset: Boolean = true)
        fun upView()
        fun pageChanged()
        fun contentLoadFinish()
    }

}