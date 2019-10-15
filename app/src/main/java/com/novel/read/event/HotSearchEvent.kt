package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.HotSearchResp
import com.novel.read.model.protocol.SearchResp

/**
 * create by zlj on 2019/6/18
 * describe:
 */
class HotSearchEvent : BaseEvent<HotSearchResp>{

    constructor(result: HotSearchResp) : super(result) {}

    constructor() {}
}
