package com.novel.read.widget.page;

/**
 * Created by zlj
 */
public class TxtChapter {

    //章节所属的小说(网络)
    String bookId;
    //章节的链接(网络)
    String link;

    //章节名(共用)
    String title;

    //章节内容在文章中的起始位置(本地)
    long start;
    //章节内容在文章中的终止位置(本地)
    long end;
    String chapterId;


    public String getBookId() {
        return bookId;
    }

    public void setBookId(String id) {
        this.bookId = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getChapterId() {
        return chapterId == null ? "" : chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }


    @Override
    public String toString() {
        return "TxtChapter{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
