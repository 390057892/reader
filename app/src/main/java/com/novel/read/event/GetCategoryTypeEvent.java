package com.novel.read.event;

import com.mango.mangolib.event.BaseEvent;
import com.novel.read.model.protocol.CategoryTypeResp;

/**
 * create by 赵利君 on 2019/6/18
 * describe:
 */
public class GetCategoryTypeEvent extends BaseEvent<CategoryTypeResp> {

    public GetCategoryTypeEvent(CategoryTypeResp result) {
        super(result);
    }

    public GetCategoryTypeEvent() {
    }
}
