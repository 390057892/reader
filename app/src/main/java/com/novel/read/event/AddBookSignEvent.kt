package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.mango.mangolib.http.ErrorResponse

/**
 * create by 赵利君 on 2019/6/25
 * describe:
 */
class AddBookSignEvent : BaseEvent<ErrorResponse> {
    constructor(result: ErrorResponse) : super(result) {}

    constructor() {}
}
