package com.novel.read.event;

import com.mango.mangolib.event.BaseEvent;
import com.novel.read.model.protocol.HotSearchResp;

/**
 * create by 赵利君 on 2019/6/18
 * describe:
 */
public class HotSearchEvent extends BaseEvent<HotSearchResp> {

    public HotSearchEvent(HotSearchResp result) {
        super(result);
    }

    public HotSearchEvent() {
    }
}
