package com.novel.read.data.db

import com.novel.read.data.db.entity.ReadRecord
import org.litepal.LitePal

class ReadRecordDao {

    fun getReadTime(bookName: String): Long? =
        LitePal.where("bookName =?", bookName).findFirst(ReadRecord::class.java)?.readTime

    fun insert(readRecord: ReadRecord) {
        readRecord.saveOrUpdate("bookId=?", readRecord.bookId.toString())
    }

    fun delete(readRecord: ReadRecord) {
        readRecord.delete()
    }

    fun getAll(): List<ReadRecord> {
        return LitePal.findAll(ReadRecord::class.java)
    }

}