package com.novel.read.model.db;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * Created by newbiechen on 17-5-10.
 * 书的章节链接(作为下载的进度数据)
 * 同时作为网络章节和本地章节 (没有找到更好分离两者的办法)
 */
public class BookChapterBean extends LitePalSupport implements Serializable {
    /**
     * title : 第一章 他叫白小纯
     * link : http://read.qidian.com/chapter/rJgN8tJ_cVdRGoWu-UQg7Q2/6jr-buLIUJSaGfXRMrUjdw2
     * unreadble : false
     */
    private String chapterId;

    private String link;

    private String title;

    //所属的下载任务
    private String taskName;

    private boolean unreadble;

    //所属的书籍
    private String bookId;

    //本地书籍参数
    private CollBookBean collBookBean;

    //本地下载参数
    private DownloadTaskBean downloadTaskBean;

    //在书籍文件中的起始位置
    private long start;

    //在书籍文件中的终止位置
    private long end;

    public BookChapterBean(String id, String link, String title, String taskName,
                           boolean unreadble, String bookId, long start, long end) {
        this.chapterId = id;
        this.link = link;
        this.title = title;
        this.taskName = taskName;
        this.unreadble = unreadble;
        this.bookId = bookId;
        this.start = start;
        this.end = end;
    }
    public BookChapterBean(String id,String title) {
        this.chapterId = id;
        this.title = title;
    }

    public BookChapterBean() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isUnreadble() {
        return unreadble;
    }

    public void setUnreadble(boolean unreadble) {
        this.unreadble = unreadble;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean getUnreadble() {
        return this.unreadble;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getId() {
        return chapterId;
    }

    public void setId(String id) {
        this.chapterId = id;
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

    public CollBookBean getCollBookBean() {
        return collBookBean;
    }

    public void setCollBookBean(CollBookBean collBookBean) {
        this.collBookBean = collBookBean;
    }

    public DownloadTaskBean getDownloadTaskBean() {
        return downloadTaskBean;
    }

    public void setDownloadTaskBean(DownloadTaskBean downloadTaskBean) {
        this.downloadTaskBean = downloadTaskBean;
    }

    @Override
    public String toString() {
        return "BookChapterBean{" +
                "chapterId='" + chapterId + '\'' +
                ", link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", taskName='" + taskName + '\'' +
                ", unreadble=" + unreadble +
                ", bookId='" + bookId + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}