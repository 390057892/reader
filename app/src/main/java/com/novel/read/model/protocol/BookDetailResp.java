package com.novel.read.model.protocol;

import com.novel.read.model.db.CollBookBean;
import com.novel.read.utlis.StringUtils;

/**
 * create by 赵利君 on 2019/6/18
 * describe:
 */
public class BookDetailResp {

    /**
     * book : {"id":1,"title":"帝逆洪荒","cover":"http://dev.duoduvip.com/uploads/20190609/408f516e930518df187acc911208f004.png","description":"    【群号536o626o6】人族至宝崆峒印逆天回到未来，带来周天附身殷商人皇纣王之身！帝辛怒吼：人族要自强，人族要自立，人族要自主！但随即帝辛现了洪荒并不是那么简单，洪荒在亘古居然被打碎了，有蛮荒，莽荒，大荒三荒并存，四荒合并成就洪荒大世界！兽皇神逆，魔祖罗T，阴阳老祖，乾坤老祖，扬眉大仙，洪荒大能纷纷出世！真龙老祖，凤凰老母，瑞麒麟三族老祖出世，镇压三族！东皇太一，妖皇帝俊设计假死，瞒过众圣，如今万妖齐聚！且看帝辛如何在万族夹击之中求生存，成就一代人族大帝，庇佑人族！<br>    各位书友要是觉得《帝逆洪荒》还不错的话请不要忘记向您QQ群和微博里的朋友推荐哦！帝逆洪荒最新章节,帝逆洪荒无弹窗,帝逆洪荒全文阅读.","hot":100,"like":20,"author":"天子辉","create_time":1560048488,"words":4637684}
     * last_article : {"id":1,"title":"第一章 我为帝辛！【求支持】","create_time":1560048488}
     */

    private BookBean book;
    private LastArticleBean last_article;
    private CollBookBean collBookBean;

    public BookBean getBook() {
        return book;
    }

    public void setBook(BookBean book) {
        this.book = book;
    }

    public LastArticleBean getLast_article() {
        return last_article;
    }

    public void setLast_article(LastArticleBean last_article) {
        this.last_article = last_article;
    }

    public static class BookBean {
        /**
         * id : 1
         * title : 帝逆洪荒
         * cover : http://dev.duoduvip.com/uploads/20190609/408f516e930518df187acc911208f004.png
         * description :     【群号536o626o6】人族至宝崆峒印逆天回到未来，带来周天附身殷商人皇纣王之身！帝辛怒吼：人族要自强，人族要自立，人族要自主！但随即帝辛现了洪荒并不是那么简单，洪荒在亘古居然被打碎了，有蛮荒，莽荒，大荒三荒并存，四荒合并成就洪荒大世界！兽皇神逆，魔祖罗T，阴阳老祖，乾坤老祖，扬眉大仙，洪荒大能纷纷出世！真龙老祖，凤凰老母，瑞麒麟三族老祖出世，镇压三族！东皇太一，妖皇帝俊设计假死，瞒过众圣，如今万妖齐聚！且看帝辛如何在万族夹击之中求生存，成就一代人族大帝，庇佑人族！<br>    各位书友要是觉得《帝逆洪荒》还不错的话请不要忘记向您QQ群和微博里的朋友推荐哦！帝逆洪荒最新章节,帝逆洪荒无弹窗,帝逆洪荒全文阅读.
         * hot : 100
         * like : 20
         * author : 天子辉
         * create_time : 1560048488
         * words : 4637684
         */

        private int id;
        private String title;
        private String cover;
        private String description;
        private int hot;
        private int like;
        private String author;
        private int create_time;
        private int words;
        private int include_image;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
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
            return description == null ? "" : StringUtils.INSTANCE.delete160(description);
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

        public String getLike() {
            return like+"%";
        }

        public void setLike(int like) {
            this.like = like;
        }

        public String getAuthor() {
            return author;
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

        public int getWords() {
            return words;
        }

        public void setWords(int words) {
            this.words = words;
        }

        public int getInclude_image() {
            return include_image;
        }

        public void setInclude_image(int include_image) {
            this.include_image = include_image;
        }
    }

    public static class LastArticleBean {
        /**
         * id : 1
         * title : 第一章 我为帝辛！【求支持】
         * create_time : 1560048488
         */

        private int id;
        private String title;
        private long create_time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title == null ? "" : title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getCreate_time() {
            return create_time;
        }

        public void setCreate_time(long create_time) {
            this.create_time = create_time;
        }
    }

    public CollBookBean getCollBookBean(){
        if (collBookBean == null){
            collBookBean = createCollBookBean();
        }
        return collBookBean;
    }

    public CollBookBean createCollBookBean(){
        CollBookBean bean = new CollBookBean();
        bean.setId(String.valueOf(getBook().getId()));
        bean.setTitle(getBook().getTitle());
        bean.setAuthor(getBook().getAuthor());
        bean.setShortIntro(getBook().getDescription());
        bean.setCover(getBook().getCover());
        bean.setInclude_image(getBook().getInclude_image());
//        bean.setHasCp(getBook().isHasCp());
//        bean.setLatelyFollower(getBook().getLatelyFollower());
//        bean.setRetentionRatio(Double.parseDouble(getBook().getRetentionRatio()));
        if (getLast_article()!=null){
            bean.setUpdated(String.valueOf(getLast_article().getCreate_time()));
            bean.setLastChapter(getLast_article().getTitle());
        }
//        bean.setChaptersCount(getBook().getChaptersCount());
        return bean;
    }
}
