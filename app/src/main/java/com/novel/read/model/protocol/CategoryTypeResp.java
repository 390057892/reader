package com.novel.read.model.protocol;

import com.novel.read.base.MyApp;
import com.novel.read.utlis.StringUtils;

import java.io.Serializable;
import java.util.List;

public class CategoryTypeResp implements Serializable {


    private List<CategoryBean> category;

    public List<CategoryBean> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryBean> category) {
        this.category = category;
    }

    public static class CategoryBean {
        /**
         * id : 1
         * title : 玄幻奇幻
         * cover :
         */

        private int id;
        private String title;
        private String cover;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return StringUtils.convertCC(title == null ? "" : title, MyApp.getContext());
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
    }
}
