package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.MarkResp

/**
 * create by 赵利君 on 2019/6/25
 * describe:
 */
class GetBookSignEvent : BaseEvent<MarkResp> {
    constructor(result: MarkResp) : super(result) {}

    constructor() {}
}
