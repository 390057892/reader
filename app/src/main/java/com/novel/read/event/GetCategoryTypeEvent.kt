package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.CategoryTypeResp

/**
 * create by zlj on 2019/6/18
 * describe:
 */
class GetCategoryTypeEvent : BaseEvent<CategoryTypeResp> {

    constructor(result: CategoryTypeResp) : super(result) {}

    constructor() {}
}
