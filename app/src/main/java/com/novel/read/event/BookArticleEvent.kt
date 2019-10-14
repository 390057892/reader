package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.BookArticleResp

/**
 * create by 赵利君 on 2019/6/18
 * describe:
 */
class BookArticleEvent : BaseEvent<BookArticleResp> {

    constructor(result: BookArticleResp) : super(result) {}

    constructor() {}
}
