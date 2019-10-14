package com.novel.read.event;

import com.mango.mangolib.event.BaseEvent;

public class FinishChapterEvent extends BaseEvent {
    public FinishChapterEvent(Object result) {
        super(result);
    }

    public FinishChapterEvent() {
    }
}
