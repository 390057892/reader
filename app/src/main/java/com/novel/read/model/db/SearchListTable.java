package com.novel.read.model.db;


import androidx.annotation.Nullable;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * create by zlj on 2019/6/19
 * describe:
 */
public class SearchListTable extends LitePalSupport implements Serializable {

    private String key;
    private long saveTime;

    public String getKey() {
        return key == null ? "" : key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj != null && obj.toString().equals(key)) {
            return true;
        }
        return super.equals(obj);
    }
}
