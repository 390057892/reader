package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.SearchResp

/**
 * create by 赵利君 on 2019/6/18
 * describe:
 */
class SearchListEvent : BaseEvent<SearchResp> {

    constructor(result: SearchResp) : super(result) {}

    constructor() {}
}
