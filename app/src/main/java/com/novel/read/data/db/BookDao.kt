package com.novel.read.data.db

import android.content.ContentValues
import com.novel.read.data.db.entity.Book
import org.litepal.LitePal


class BookDao {

    fun getAllBooks(): MutableList<Book> = LitePal.findAll(Book::class.java)

    fun lastReadBook(): Book = LitePal.order("durChapterTime desc").findFirst(Book::class.java)

    fun update(book: Book) {
        val values = ContentValues()
        values.put("authorPenname", book.authorPenname)
        values.put("bookName", book.bookName)
        values.put("bookStatus", book.bookStatus)
        values.put("categoryName", book.categoryName)
        values.put("channelName", book.channelName)
        values.put("cName", book.cName)
        values.put("coverImageUrl", book.coverImageUrl)
        values.put("introduction", book.introduction)
        values.put("keyWord", book.keyWord)
        values.put("lastUpdateChapterDate", book.lastUpdateChapterDate)
        values.put("status", book.status)
        values.put("wordCount", book.wordCount)
        values.put("durChapterTime", book.durChapterTime)
        values.put("durChapterIndex", book.durChapterIndex)
        values.put("durChapterPos", book.durChapterPos)
        values.put("durChapterTitle", book.durChapterTitle)
        values.put("totalChapterNum", book.totalChapterNum)
        LitePal.updateAll(Book::class.java, values, "bookId=?", book.bookId.toString())
    }

    fun getBook(bookId: String): Book? =
        LitePal.where("bookId=?", bookId).findFirst(Book::class.java)

    fun insert(book: Book) = book.saveOrUpdate("bookId=?", book.bookId.toString())

    fun delete(book: Book): Int =
        LitePal.deleteAll(Book::class.java, "bookId=?", book.bookId.toString())

    fun saveBook(book: List<Book>?) {
        if (book != null && book.isNotEmpty()) {
            LitePal.saveAll(book)
        }
    }

}