package com.novel.read.data.db

class BookDatabase private constructor() {

    companion object {
        private var instance: BookDatabase? = null
            get() {
                if (field == null) {
                    field = BookDatabase()
                }
                return field
            }

        fun get(): BookDatabase {
            return instance!!
        }
    }

    private var chapterDao: ChapterDao? = null
    private var bookDao: BookDao? = null
    private var readRecordDao: ReadRecordDao? = null
    private var bookMarkDao: BookMarkDao? = null
    private var searchHistoryDao: SearchHistoryDao? = null
    private var userDao: UserDao? = null

    fun getChapterDao(): ChapterDao {
        if (chapterDao == null) {
            chapterDao = ChapterDao()
        }
        return chapterDao!!
    }

    fun getBookDao(): BookDao {
        if (bookDao == null) {
            bookDao = BookDao()
        }
        return bookDao!!
    }

    fun getReadRecordDao(): ReadRecordDao {
        if (readRecordDao == null) {
            readRecordDao = ReadRecordDao()
        }
        return readRecordDao!!
    }

    fun getBookMarkDao(): BookMarkDao {
        if (bookMarkDao == null) {
            bookMarkDao = BookMarkDao()
        }
        return bookMarkDao!!
    }

    fun getSearchDao(): SearchHistoryDao {
        if (searchHistoryDao == null) {
            searchHistoryDao = SearchHistoryDao()
        }
        return searchHistoryDao!!
    }

    fun getUserDao(): UserDao {
        if (userDao == null) {
            userDao = UserDao()
        }
        return userDao!!
    }
}