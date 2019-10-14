package com.novel.read.model.protocol;


import com.novel.read.base.MyApp;
import com.novel.read.utlis.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * create by 赵利君 on 2019/6/18
 * describe:
 */
public class RecommendBookResp implements Serializable {


    private List<BookBean> book;

    public List<BookBean> getBook() {
        return book;
    }

    public void setBook(List<BookBean> book) {
        this.book = book;
    }

    public static class BookBean {
        /**
         * id : 139
         * title : 丹师剑宗
         * cover : http://dev.duoduvip.com/uploads/20190611/b81d831d3310041846444dacca57cef9.png
         * description :
         * hot : 83146
         * like : 64
         * author : 伯爵
         * create_time : 1560191131
         */

        private int id;
        private String title;
        private String cover;
        private String description;
        private int hot;
        private int like;
        private String author;
        private int create_time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title == null ? "" : StringUtils.convertCC(title, MyApp.getContext());
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getDescription() {
            return description == null ? "" : StringUtils.convertCC(StringUtils.delete160(description), MyApp.getContext());
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getHot() {
            return hot;
        }

        public void setHot(int hot) {
            this.hot = hot;
        }

        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public String getAuthor() {
            return author == null ? "" : StringUtils.convertCC(author, MyApp.getContext()) ;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }
    }
}
