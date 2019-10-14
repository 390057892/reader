package com.novel.read.model.db;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by newbiechen on 17-5-11.
 */
public class DownloadTaskBean extends LitePalSupport {
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_WAIT = 2;
    public static final int STATUS_PAUSE = 3;
    public static final int STATUS_ERROR = 4;
    public static final int STATUS_FINISH = 5;

    //任务名称 -> 名称唯一不重复
    private String taskName;
    //所属的bookId(外健)
    private String bookId;

    private List<BookChapterBean> bookChapterList = new ArrayList<>();
    //章节的下载进度,默认为初始状态
    private int currentChapter = 0;
    //最后的章节
    private int lastChapter = 0;
    //状态:正在下载、下载完成、暂停、等待、下载错误。

    private volatile int status = STATUS_WAIT;
    //总大小 -> (完成之后才会赋值)
    private long size = 0;
    private CollBookBean collBookBean;


    public DownloadTaskBean(String taskName, String bookId, int currentChapter, int lastChapter,
                            int status, long size) {
        this.taskName = taskName;
        this.bookId = bookId;
        this.currentChapter = currentChapter;
        this.lastChapter = lastChapter;
        this.status = status;
        this.size = size;
    }

    public DownloadTaskBean() {
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
        if (bookChapterList!=null){
            for (BookChapterBean bean : bookChapterList){
                bean.setTaskName(getTaskName());
            }
        }
    }

    public List<BookChapterBean> getBookChapterList() {
        if (bookChapterList == null) {

//            BookChapterBeanDao targetDao = daoSession.getBookChapterBeanDao();
//            List<BookChapterBean> bookChapterListNew = targetDao
//                    ._queryDownloadTaskBean_BookChapterList(taskName);

            List<BookChapterBean> bookChapterListNew = LitePal
                    .where("taskName=?", taskName).find(BookChapterBean.class);

            synchronized (this) {
                if (bookChapterList == null) {
                    bookChapterList = bookChapterListNew;
                }
            }
        }
        return bookChapterList;
    }

    /**
     * 这才是真正的列表使用类。
     *
     */
    public void setBookChapters(List<BookChapterBean> beans){
        bookChapterList = beans;
        for (BookChapterBean bean : bookChapterList){
            bean.setTaskName(getTaskName());
        }
    }

    public List<BookChapterBean> getBookChapters(){
        return bookChapterList;

    }

    public int getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(int current) {
        this.currentChapter = current;
    }

    public int getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(int lastChapter) {
        this.lastChapter = lastChapter;
    }

    //多线程访问的问题，所以需要同步机制
    public int getStatus() {
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public synchronized void resetBookChapterList() {
        bookChapterList = null;
    }


    public CollBookBean getCollBookBean() {

        List<CollBookBean> list = LitePal.where("bookId=?", bookId).find(CollBookBean.class);
        if (list!=null&&list.size()>0){
            return list.get(0);
        }else {
            return collBookBean;
        }
    }

    public void setCollBookBean(CollBookBean collBookBean) {
        this.collBookBean = collBookBean;
    }
}
