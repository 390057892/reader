package com.novel.read.event;

import com.mango.mangolib.event.BaseEvent;
import com.novel.read.model.protocol.RankByUpadateResp;

/**
 * create by 赵利君 on 2019/6/18
 * describe:
 */
public class RankByUpdateEvent extends BaseEvent<RankByUpadateResp> {

    public RankByUpdateEvent(RankByUpadateResp result) {
        super(result);
    }

    public RankByUpdateEvent() {
    }
}
