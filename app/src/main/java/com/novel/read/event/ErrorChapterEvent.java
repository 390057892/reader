package com.novel.read.event;

import com.mango.mangolib.event.BaseEvent;

public class ErrorChapterEvent extends BaseEvent {
    public ErrorChapterEvent(Object result) {
        super(result);
    }

    public ErrorChapterEvent() {
    }
}
