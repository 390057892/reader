package com.novel.read.event;

import com.mango.mangolib.event.BaseEvent;
import com.mango.mangolib.http.ErrorResponse;

public class DeleteBookSignEvent extends BaseEvent<ErrorResponse> {
    public DeleteBookSignEvent(ErrorResponse result) {
        super(result);
    }

    public DeleteBookSignEvent() {
    }
}
