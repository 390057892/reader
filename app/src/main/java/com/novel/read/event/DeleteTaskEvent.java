package com.novel.read.event;

import com.mango.mangolib.event.BaseEvent;
import com.novel.read.model.db.CollBookBean;

/**
 * Created by newbiechen on 17-5-27.
 */

public class DeleteTaskEvent extends BaseEvent {
    public CollBookBean collBook;

    public DeleteTaskEvent(CollBookBean collBook){
        this.collBook = collBook;
    }
}
