package com.novel.read.service;

import com.mango.mangolib.event.BaseEvent;

/**
 * Created by newbiechen on 17-5-10.
 * 下载进度事件
 */

public class DownloadMessage extends BaseEvent {

    public String message;

    public DownloadMessage(String message){
        this.message = message;
    }
}
