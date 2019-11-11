package com.novel.read.model.protocol;

import com.novel.read.base.MyApp;
import com.novel.read.utlis.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * create by 赵利君 on 2019/6/20
 * describe:
 */
public class RankByUpdateResp implements Serializable {


    private List<BookBean> book;

    public List<BookBean> getBook() {
        if (book == null) {
            return new ArrayList<>();
        }
        return book;
    }

    public void setBook(List<BookBean> book) {
        this.book = book;
    }

    public static class BookBean {
        /**
         * id : 2446
         * title : 吞天记
         * cover : http://api.duoduvip.com/uploads/nocover.jpg
         * description : &#160;&#160;&#160;&#160;炎黄古域，浩瀚无边，无尽岁月中诞生诸多太古仙妖，撕裂天地，脱三界五行。更有万物神灵，天生神体，穿梭虚空，逆乱阴阳，无所不能。<br>&#160;&#160;&#160;&#160;当今乃仙道盛世，万法通天，众生修道，妖孽横行！<br>&#160;&#160;&#160;&#160;东吴太子吴煜，于绝境中得东方绝世战仙之衣钵，自此横空出世，逆天崛起。<br>&#160;&#160;&#160;&#160;亿万世人心中，他是普渡众生的帝仙！<br>&#160;&#160;&#160;&#160;漫天仙佛眼中，他是吞噬天地的妖魔！<br>&#160;&#160;&#160;&#160;……<br>&#160;&#160;&#160;&#160;想和作者探讨剧情，加入讨论群，请加我微信：fengqingyang17k。或搜风青阳。<br>&#160;&#160;&#160;&#160;各位书友要是觉得《吞天记》还不错的话请不要忘记向您QQ群和微博里的朋友推荐哦！吞天记最新章节,吞天记无弹窗,吞天记全文阅读.
         * hot : 41926
         * like : 65
         * author : 风青阳
         * create_time : 1561001132
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
            return title == null ? "" : StringUtils.INSTANCE.convertCC(title);
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
            return description == null ? "" : StringUtils.INSTANCE.convertCC(StringUtils.INSTANCE.delete160(description));
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
            return author == null ? "" : StringUtils.INSTANCE.convertCC(author);
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
