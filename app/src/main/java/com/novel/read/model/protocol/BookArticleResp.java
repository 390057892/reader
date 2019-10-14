package com.novel.read.model.protocol;

import com.novel.read.model.db.BookChapterBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookArticleResp implements Serializable {


    private List<ArticleBean> article;
    private List<BookChapterBean> bookChapterBean;

    public List<ArticleBean> getArticle() {
        return article;
    }

    public void setArticle(List<ArticleBean> article) {
        this.article = article;
    }

    public static class ArticleBean {
        /**
         * id : 1
         * title : 第一章 我为帝辛！【求支持】
         * words : 4272
         * create_time : 1560048488
         */

        private int id;
        private String title;
        private int words;
        private int create_time;
        private String volume;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return getVolume()+title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getWords() {
            return words;
        }

        public void setWords(int words) {
            this.words = words;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public String getVolume() {
            return volume == null ? "" : volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }
    }

    public List<BookChapterBean> getChapterBean() {
        if (bookChapterBean == null) {
            bookChapterBean = createChapterBean();
        }
        return bookChapterBean;
    }

    public List<BookChapterBean> createChapterBean() {
        List<BookChapterBean> mList = new ArrayList<>();
        for (ArticleBean articleBean:getArticle()){
            mList.add(new BookChapterBean(String.valueOf(articleBean.getId()),articleBean.getTitle()));
        }
        return mList;
    }
}
