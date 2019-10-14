package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.RecommendListResp

/**
 * create by 赵利君 on 2019/6/18
 * describe:
 */
class RecommendListEvent : BaseEvent<RecommendListResp> {

    constructor(result: RecommendListResp) : super(result) {}

    constructor() {}
}
