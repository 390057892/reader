package com.novel.read.event;

import com.mango.mangolib.event.BaseEvent;
import com.novel.read.model.db.CollBookBean;

/**
 * Created by newbiechen on 17-5-27.
 */

public class DeleteResponseEvent extends BaseEvent {
    public boolean isDelete;
    public CollBookBean collBook;
    public DeleteResponseEvent(boolean isDelete, CollBookBean collBook){
        this.isDelete = isDelete;
        this.collBook = collBook;
    }
}
