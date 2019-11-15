package com.novel.read.service;

import com.mango.mangolib.event.BaseEvent;

public class DownloadMessage extends BaseEvent {

    public String message;

    public DownloadMessage(String message){
        this.message = message;
    }
}
