package com.novel.read.data.db

import com.novel.read.data.db.entity.BookChapter
import org.litepal.LitePal

class ChapterDao {

    fun observeByBook(bookId: String): List<BookChapter>? =
        LitePal.where("bookId=?", bookId.toString()).find(BookChapter::class.java)

    fun liveDataSearch(bookId: String, key: String): List<BookChapter>? =
        LitePal.where("bookId=? and chapterName like ?", bookId, "%${key}%")
            .find(BookChapter::class.java)

    fun getChapter(chapterId: Long): BookChapter? =
        LitePal.where("chapterId =?", chapterId.toString()).findFirst(BookChapter::class.java)

    fun getChapter(bookId: Long, chapterIndex: Int): BookChapter? =
        LitePal.where("bookId=? and chapterIndex =?", bookId.toString(), chapterIndex.toString())
            .findFirst(BookChapter::class.java)

    fun getChapterList(bookId: Long): List<BookChapter>? =
        LitePal.where("bookId=?", bookId.toString()).find(BookChapter::class.java)

    fun getChapterList(bookId: Long, start: Int, end: Int): List<BookChapter> =
        LitePal.where(
            "bookId=? and chapterIndex >=? and chapterIndex <=?",
            bookId.toString(),
            start.toString(),
            end.toString()
        ).order("chapterIndex desc").find(BookChapter::class.java)

    fun getChapterCount(bookId: Long): Int {
       return  LitePal.where("bookId=?", bookId.toString()).count(BookChapter::class.java)
    }

    fun insert(bookChapters: Array<BookChapter>) {
        if (bookChapters.isNotEmpty()) {
            LitePal.deleteAll(
                BookChapter::class.java,
                "bookId=?",
                bookChapters[0].bookId.toString()
            )
        }
        LitePal.saveAll(bookChapters.toList())
    }

}