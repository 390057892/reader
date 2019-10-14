package com.novel.read.event;

import com.mango.mangolib.event.BaseEvent;

public class UpdateBookEvent extends BaseEvent<String> {

    public UpdateBookEvent(String result) {
        super(result);
    }

    public UpdateBookEvent() {
    }
}
