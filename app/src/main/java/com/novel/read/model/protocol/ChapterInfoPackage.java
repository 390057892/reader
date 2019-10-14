package com.novel.read.model.protocol;

import com.novel.read.model.db.ChapterInfoBean;

import java.io.Serializable;
import java.util.List;

public class ChapterInfoPackage implements Serializable {

    private List<ChapterInfoBean> article;

    public List<ChapterInfoBean> getArticle() {
        return article;
    }

    public void setArticle(List<ChapterInfoBean> article) {
        this.article = article;
    }


}
