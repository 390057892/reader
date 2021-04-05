package com.novel.read.data.db

import com.novel.read.data.db.entity.Bookmark
import org.litepal.LitePal

class BookMarkDao {

    fun observeByBook(bookId: Long): List<Bookmark>? =
        LitePal.where("bookId=?", bookId.toString()).find(Bookmark::class.java)

    fun liveDataSearch(bookId: String, key: String): List<Bookmark>? =
        LitePal.where(
            "bookId=? and (chapterName like ? or content like ?)",
            bookId.toString(),
            "%${key}%", "%${key}%"
        )
            .find(Bookmark::class.java)

    fun insert(bookmark: Bookmark) {
        bookmark.saveOrUpdate(
            "bookId=? and chapterIndex=?",
            bookmark.bookId.toString(),
            bookmark.chapterIndex.toString()
        )
    }

    fun update(bookmark: Bookmark) {
        bookmark.saveOrUpdate(
            "bookId=? and chapterIndex=?",
            bookmark.bookId.toString(),
            bookmark.chapterIndex.toString()
        )
    }

    fun delete(bookmark: Bookmark) {
        bookmark.delete()
    }

}