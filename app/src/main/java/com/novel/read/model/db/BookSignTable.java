package com.novel.read.model.db;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * create by zlj on 2019/11/6
 * describe: 书签数据库
 */
public class BookSignTable extends LitePalSupport implements Serializable {

    private String bookId;
    private String content;
    private long saveTime;

}
