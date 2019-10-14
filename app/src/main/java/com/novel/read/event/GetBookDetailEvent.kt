package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.BookDetailResp

/**
 * create by 赵利君 on 2019/6/18
 * describe:
 */
class GetBookDetailEvent : BaseEvent<BookDetailResp> {

    constructor(result: BookDetailResp) : super(result) {}

    constructor() {}
}
