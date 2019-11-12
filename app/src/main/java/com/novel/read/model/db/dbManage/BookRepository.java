package com.novel.read.model.db.dbManage;

import android.content.ContentValues;
import android.util.Log;

import com.novel.read.constants.Constant;
import com.novel.read.model.db.BookChapterBean;
import com.novel.read.model.db.BookRecordBean;
import com.novel.read.model.db.BookSignTable;
import com.novel.read.model.db.ChapterInfoBean;
import com.novel.read.model.db.CollBookBean;
import com.novel.read.model.db.DownloadTaskBean;
import com.novel.read.utlis.FileUtils;
import com.novel.read.utlis.IOUtils;
import com.novel.read.utlis.SpUtil;
import com.novel.read.widget.page.Void;

import org.litepal.LitePal;
import org.litepal.crud.callback.SaveCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Created by newbiechen on 17-5-8.
 * 存储关于书籍内容的信息(CollBook(收藏书籍),BookChapter(书籍列表),ChapterInfo(书籍章节),BookRecord(记录),BookSignTable书签)
 */

public class BookRepository {
    private static final String TAG = "CollBookManager";
    private static volatile BookRepository sInstance;

    private BookRepository() {
    }

    public static BookRepository getInstance() {
        if (sInstance == null) {
            synchronized (BookRepository.class) {
                if (sInstance == null) {
                    sInstance = new BookRepository();
                }
            }
        }
        return sInstance;
    }

    //存储已收藏书籍
    public void saveCollBookWithAsync(CollBookBean bean) {

        bean.saveOrUpdate("bookId=?", bean.getId());
        for (int i = 0; i < bean.getBookChapters().size(); i++) {
            bean.getBookChapters().get(i).setCollBookBean(bean);
        }
        LitePal.saveAllAsync(bean.getBookChapters()).listen(new SaveCallback() {
            @Override
            public void onFinish(boolean success) {
                Log.e(TAG, "saveCollBookWithAsync: " + success);
            }
        });
    }

    /**
     * 异步存储。
     * 同时保存BookChapter
     *
     * @param beans
     */
    public void saveCollBooksWithAsync(List<CollBookBean> beans) {
//        mSession.startAsyncSession()
//                .runInTx(
//                        () -> {
//                            for (CollBookBean bean : beans){
//                                if (bean.getBookChapters() != null){
//                                    //存储BookChapterBean(需要修改，如果存在id相同的则无视)
//                                    mSession.getBookChapterBeanDao()
//                                            .insertOrReplaceInTx(bean.getBookChapters());
//                                }
//                            }
//                            //存储CollBook (确保先后顺序，否则出错)
//                            mCollBookDao.insertOrReplaceInTx(beans);
//                        }
//                );
    }

    public void saveCollBook(CollBookBean bean) {
        ContentValues values = new ContentValues();
        values.put("isUpdate", bean.isUpdate());
        values.put("lastRead", bean.getLastRead());
        values.put("lastChapter", bean.getLastChapter());
        LitePal.updateAll(CollBookBean.class, values, "bookId=?", bean.getId());
    }

    public void saveCollBooks(List<CollBookBean> beans) {
        for (int i = 0; i < beans.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("isUpdate", beans.get(i).isUpdate());
            values.put("lastRead", beans.get(i).getLastRead());
            values.put("lastChapter", beans.get(i).getLastChapter());
            values.put("updated", beans.get(i).getUpdated());
            LitePal.updateAll(CollBookBean.class, values, "bookId=?", beans.get(i).getId());
        }
    }

    /**
     * 异步存储BookChapter
     *
     * @param beans
     */
    public void saveBookChaptersWithAsync(List<BookChapterBean> beans, CollBookBean collBookBean) {
        collBookBean.saveOrUpdate("bookId=?", collBookBean.getId());
        for (int i = 0; i < collBookBean.getBookChapters().size(); i++) {
            collBookBean.getBookChapters().get(i).setCollBookBean(collBookBean);
            collBookBean.getBookChapters().get(i).saveOrUpdateAsync("bookId=?", collBookBean.getId());
        }
    }

    /**
     * 存储章节
     *
     * @param folderName
     * @param fileName
     * @param content
     */
    public void saveChapterInfo(String folderName, String fileName, String content) {
        String str = content.replaceAll("\\\\n\\\\n", "\n");
        File file = BookManager.getBookFile(folderName, fileName);
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(str);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.INSTANCE.close(writer);
        }
    }

    public void saveBookRecord(BookRecordBean bean) {
        bean.saveOrUpdateAsync("bookId=?", bean.getBookId()).listen(new SaveCallback() {
            @Override
            public void onFinish(boolean success) {
                Log.e("saveBookRecord", "onFinish: " + success);
            }
        });
    }

    /*****************************get************************************************/
    public CollBookBean getCollBook(String bookId) {
        List<CollBookBean> bean = LitePal.where("bookId =?", bookId).find(CollBookBean.class);
        if (bean != null && bean.size() > 0) {
            return bean.get(0);
        } else {
            return null;
        }
    }


    public List<CollBookBean> getCollBooks() {
        List<CollBookBean> collBookBeans;
        if (SpUtil.getBooleanValue(Constant.BookSort, false)) { //默认根据阅读时间排序
            collBookBeans = LitePal.order("updated desc").find(CollBookBean.class);
        } else {
            collBookBeans = LitePal.order("lastRead desc").find(CollBookBean.class);
        }
        return collBookBeans;
    }


    //获取书籍列表
    public List<BookChapterBean> getBookChaptersInRx(String bookId) {
        List<CollBookBean> bookBeans = LitePal.where("bookId=?", bookId).find(CollBookBean.class, true);
        if (bookBeans != null && bookBeans.size() > 0) {
            return bookBeans.get(0).getBookChapters();
        } else {
            return new ArrayList<>();
        }
    }

    //获取阅读记录
    public BookRecordBean getBookRecord(String bookId) {
        List<BookRecordBean> beans = LitePal.where("bookId=?", bookId).find(BookRecordBean.class);
        if (beans != null && beans.size() > 0) {
            return beans.get(0);
        } else {
            return null;
        }
    }

    //TODO:需要进行获取编码并转换的问题
    public ChapterInfoBean getChapterInfoBean(String folderName, String fileName) {
        File file = new File(Constant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_NB);
        if (!file.exists()) return null;
        Reader reader = null;
        String str = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.INSTANCE.close(reader);
        }

        ChapterInfoBean bean = new ChapterInfoBean();
        bean.setTitle(fileName);
        bean.setBody(sb.toString());
        return bean;
    }

    /************************************************************/
    /************************************************************/
    public Single<Void> deleteCollBookInRx(CollBookBean bean) {
        return Single.create(new SingleOnSubscribe<Void>() {
            @Override
            public void subscribe(SingleEmitter<Void> e) throws Exception {
                //查看文本中是否存在删除的数据
                deleteBook(bean.getId());
                //删除任务
                deleteDownloadTask(bean.getId());
                //删除目录
                deleteBookChapter(bean.getId());
                //删除CollBook
                LitePal.deleteAll(CollBookBean.class, "bookId=?", bean.getId());
                e.onSuccess(new Void());
            }
        });
    }

    //这个需要用rx，进行删除
    public void deleteBookChapter(String bookId) {
//        mSession.getBookChapterBeanDao()
//                .queryBuilder()
//                .where(BookChapterBeanDao.Properties.BookId.eq(bookId))
//                .buildDelete()
//                .executeDeleteWithoutDetachingEntities();
    }

    public void deleteCollBook(CollBookBean collBook) {
//        mCollBookDao.delete(collBook);
    }

    //删除书籍
    public void deleteBook(String bookId) {
        FileUtils.deleteFile(Constant.INSTANCE.BOOK_CACHE_PATH + bookId);
    }

    public void deleteBookRecord(String id) {
//        mSession.getBookRecordBeanDao()
//                .queryBuilder()
//                .where(BookRecordBeanDao.Properties.BookId.eq(id))
//                .buildDelete()
//                .executeDeleteWithoutDetachingEntities();
    }

    //删除任务
    public void deleteDownloadTask(String bookId) {
//        mSession.getDownloadTaskBeanDao()
//                .queryBuilder()
//                .where(DownloadTaskBeanDao.Properties.BookId.eq(bookId))
//                .buildDelete()
//                .executeDeleteWithoutDetachingEntities();
    }

    public List<DownloadTaskBean> getDownloadTaskList() {
        return LitePal.findAll(DownloadTaskBean.class);
    }


    public void saveDownloadTask(DownloadTaskBean bean) {
        bean.saveOrUpdate("bookId=?", bean.getBookId());
        CollBookBean collBookBean = bean.getCollBookBean();
        for (int i = 0; i < bean.getBookChapters().size(); i++) {
            bean.getBookChapters().get(i).setDownloadTaskBean(bean);
            bean.getBookChapters().get(i).setCollBookBean(collBookBean);
            bean.getBookChapters().get(i).saveOrUpdate("chapterId=?", bean.getBookChapters().get(i).getId());

        }
    }

    /**
     * 获取本地书签
     *
     * @param bookId 书籍Id
     * @return 书签
     */
    public List<BookSignTable> getSign(String bookId) {
        return LitePal.where("bookId=?", bookId).find(BookSignTable.class);
    }

    /**
     * 添加书签
     *
     * @param bookId    bookId
     * @param articleId articleId
     * @param content   content
     */
    public void addSign(String bookId, String articleId, String content) {
        BookSignTable bookSignTable = new BookSignTable(bookId, articleId, content);
        bookSignTable.saveOrUpdate();
    }

    /**
     * 删除书签
     *
     * @param articleId articleId
     */
    public void deleteSign(String articleId) {
        LitePal.deleteAll(BookSignTable.class, "articleId=?", articleId);
    }

    /**
     * 根据id查询书签是否存在
     *
     * @param articleId
     */
    public boolean getSignById(String articleId) {
        List<BookSignTable> mList = LitePal.where("articleId=?", articleId).find(BookSignTable.class);
        return mList.size() > 0;
    }
}
