package com.novel.read.model.protocol;

import java.util.List;

public class MarkResp {

    private List<SignBean> sign;

    public List<SignBean> getSign() {
        return sign;
    }

    public void setSign(List<SignBean> sign) {
        this.sign = sign;
    }

    public static class SignBean {
        /**
         * id : 8
         * uid : 1
         * book_id : 1
         * article_id : 1
         * words : 0
         * create_time : 1561450031
         * update_time : 1561450031
         * delete_time : null
         */

        private int id;
        private int uid;
        private int book_id;
        private int article_id;
        private int words;
        private int create_time;
        private int update_time;
        private Object delete_time;
        private String content;
        private boolean edit;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getBook_id() {
            return book_id;
        }

        public void setBook_id(int book_id) {
            this.book_id = book_id;
        }

        public int getArticle_id() {
            return article_id;
        }

        public void setArticle_id(int article_id) {
            this.article_id = article_id;
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

        public String getContent() {
            return content == null ? "" : content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isEdit() {
            return edit;
        }

        public void setEdit(boolean edit) {
            this.edit = edit;
        }
    }
}
