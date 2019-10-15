package com.novel.read.model.db.dbManage;

import android.content.ContentValues;
import android.util.Log;

import com.novel.read.constants.Constant;
import com.novel.read.model.db.BookChapterBean;
import com.novel.read.model.db.BookRecordBean;
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
 * 存储关于书籍内容的信息(CollBook(收藏书籍),BookChapter(书籍列表),ChapterInfo(书籍章节),BookRecord(记录))
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

//        bean.saveAsync().listen(new SaveCallback() {
//            @Override
//            public void onFinish(boolean success) {
//                System.out.println(success);
//            }
//        });
//        LitePal.saveAll(bean.getBookChapters());
//        bean.saveOrUpdate("bookId=?", bean.getId());
//        bean.saveOrUpdateAsync("bookId=?", bean.getId());
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
//        LitePal.saveAll(bean.getBookChapters());
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
//        mCollBookDao.insertOrReplace(bean);
        ContentValues values = new ContentValues();
        values.put("isUpdate", bean.isUpdate());
        values.put("lastRead", bean.getLastRead());
        values.put("lastChapter", bean.getLastChapter());
        LitePal.updateAll(CollBookBean.class, values, "bookId=?", bean.getId());
//        bean.setBookChapters(new ArrayList<>());
//        bean.saveOrUpdate("bookId=?", bean.getId());
//        for (int i = 0; i < bean.getBookChapters().size(); i++) {
//            bean.getBookChapters().get(i).setCollBookBean(bean);
//        }
//        LitePal.saveAllAsync(bean.getBookChapters()).listen(new SaveCallback() {
//            @Override
//            public void onFinish(boolean success) {
//                Log.e(TAG, "saveCollBookWithAsync: " + success);
//            }
//        });
    }

    public void saveCollBooks(List<CollBookBean> beans) {
        for (int i = 0; i <beans.size() ; i++) {
            ContentValues values = new ContentValues();
            values.put("isUpdate", beans.get(i).isUpdate());
            values.put("lastRead", beans.get(i).getLastRead());
            values.put("lastChapter", beans.get(i).getLastChapter());
            values.put("updated",beans.get(i).getUpdated());
            LitePal.updateAll(CollBookBean.class, values, "bookId=?", beans.get(i).getId());
        }
    }

    /**
     * 异步存储BookChapter
     *
     * @param beans
     */
    public void saveBookChaptersWithAsync(List<BookChapterBean> beans, CollBookBean collBookBean) {

//        //先删除旧的章节，再异步储存新的章节
//        LitePal.deleteAll(BookChapterBean.class, "bookId=?", collBookBean.getId());
        collBookBean.saveOrUpdate("bookId=?", collBookBean.getId());
        for (int i = 0; i <collBookBean.getBookChapters().size() ; i++) {
            collBookBean.getBookChapters().get(i).setCollBookBean(collBookBean);
            collBookBean.getBookChapters().get(i).saveOrUpdateAsync("bookId=?", collBookBean.getId());

        }

//        LitePal.saveAllAsync(beans).listen(new SaveCallback() {
//            @Override
//            public void onFinish(boolean success) {
//                if (success) {
//
//                }
//                Log.d(TAG, "saveBookChaptersWithAsync: " + "进行存储" + success);
//            }
//        });
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
            IOUtils.close(writer);
        }
    }

    public void saveBookRecord(BookRecordBean bean) {
//        mSession.getBookRecordBeanDao()
//                .insertOrReplace(bean);
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
//        return mCollBookDao
//                .queryBuilder()
//                .orderDesc(CollBookBeanDao.Properties.LastRead)
//                .list();
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
//        return Single.create(new SingleOnSubscribe<List<BookChapterBean>>() {
//            @Override
//            public void subscribe(SingleEmitter<List<BookChapterBean>> e) throws Exception {
//                List<BookChapterBean> beans = mSession
//                        .getBookChapterBeanDao()
//                        .queryBuilder()
//                        .where(BookChapterBeanDao.Properties.BookId.eq(bookId))
//                        .list();
//                e.onSuccess(beans);
//            }
//        });
        List<CollBookBean> bookBeans = LitePal.where("bookId=?", bookId).find(CollBookBean.class, true);
        if (bookBeans != null && bookBeans.size() > 0) {
            return bookBeans.get(0).getBookChapters();
        } else {
            return new ArrayList<>();
        }
    }

    //获取阅读记录
    public BookRecordBean getBookRecord(String bookId) {
//        return mSession.getBookRecordBeanDao()
//                .queryBuilder()
//                .where(BookRecordBeanDao.Properties.BookId.eq(bookId))
//                .unique();
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
            IOUtils.close(reader);
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
        bean.saveOrUpdate("bookId=?",bean.getBookId());
        CollBookBean collBookBean = bean.getCollBookBean();
        for (int i = 0; i <bean.getBookChapters().size() ; i++) {
            bean.getBookChapters().get(i).setDownloadTaskBean(bean);
            bean.getBookChapters().get(i).setCollBookBean(collBookBean);
            bean.getBookChapters().get(i).saveOrUpdate("chapterId=?", bean.getBookChapters().get(i).getId());

        }


//        //先删除旧的章节，再异步储存新的章节
//        LitePal.deleteAll(BookChapterBean.class, "taskName=?",bean.getTaskName());
//
//        LitePal.saveAllAsync(bean.getBookChapters()).listen(new SaveCallback() {
//            @Override
//            public void onFinish(boolean success) {
//                if (success) {
//                    bean.setBookChapters(bean.getBookChapters());
//                    bean.saveOrUpdate("taskName=?",bean.getTaskName());
//                }
//                Log.d(TAG, "saveBookChaptersWithAsync: " + "进行存储" + success);
//            }
//        });
    }
}
