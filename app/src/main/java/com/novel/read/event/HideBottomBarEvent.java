package com.novel.read.event;

import com.mango.mangolib.event.BaseEvent;

public class HideBottomBarEvent extends BaseEvent<Boolean> {
    public HideBottomBarEvent(Boolean result) {
        super(result);
    }

    public HideBottomBarEvent() {
    }
}
