package com.novel.read.model.protocol;

import java.io.Serializable;
import java.util.List;

public class HotSearchResp implements Serializable {


    private List<String> key;

    public List<String> getKey() {
        return key;
    }

    public void setKey(List<String> key) {
        this.key = key;
    }
}
