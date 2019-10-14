package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.RecommendBookResp

/**
 * create by 赵利君 on 2019/6/18
 * describe:
 */
class GetRecommendBookEvent : BaseEvent<RecommendBookResp> {

    constructor(result: RecommendBookResp) : super(result) {}

    constructor() {}
}
