package com.novel.read.model.protocol;

import com.novel.read.base.MyApp;
import com.novel.read.utlis.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * create by 赵利君 on 2019/6/19
 * describe:
 */
public class RecommendListResp implements Serializable {


    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 5
         * book_id : 10
         * type : 1
         * gender : 1
         * sort : 10
         * create_time : 1560129944
         * update_time : 1560129944
         * delete_time : null
         * book_title : 重生之末世宝典
         * book_cover : http://api.duoduvip.com/uploads/nocover.jpg
         */

        private int id;
        private int book_id;
        private int type;
        private int gender;
        private int sort;
        private int create_time;
        private int update_time;
        private Object delete_time;
        private String book_title;
        private String book_cover;
        private String author;
        private String description;
        private int hot;
        private int like;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getBook_id() {
            return book_id;
        }

        public void setBook_id(int book_id) {
            this.book_id = book_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public Object getDelete_time() {
            return delete_time;
        }

        public void setDelete_time(Object delete_time) {
            this.delete_time = delete_time;
        }

        public String getBook_title() {
            return book_title == null ? "" : StringUtils.convertCC(book_title, MyApp.getContext());
        }

        public void setBook_title(String book_title) {
            this.book_title = book_title;
        }

        public String getBook_cover() {
            return book_cover == null ? "" : book_cover;
        }

        public void setBook_cover(String book_cover) {
            this.book_cover = book_cover;
        }

        public String getAuthor() {
            return author == null ? "" : StringUtils.convertCC(author, MyApp.getContext());
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDescription() {
            return description == null ? "" : StringUtils.convertCC(StringUtils.delete160(description), MyApp.getContext());
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getHot() {
            return String.valueOf(hot);
        }

        public void setHot(int hot) {
            this.hot = hot;
        }

        public String getLike() {
            return like+"%";
        }

        public void setLike(int like) {
            this.like = like;
        }
    }
}
