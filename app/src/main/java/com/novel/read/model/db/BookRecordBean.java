package com.novel.read.model.db;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * Created by newbiechen on 17-5-20.
 */
public class BookRecordBean extends LitePalSupport implements Serializable {
    //所属的书的id
    private String bookId;
    //阅读到了第几章
    private int chapter;
    //当前的页码
    private int pagePos;



    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getPagePos() {
        return pagePos;
    }

    public void setPagePos(int pagePos) {
        this.pagePos = pagePos;
    }
}
