package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.mango.mangolib.http.ErrorResponse

class DeleteBookSignEvent : BaseEvent<ErrorResponse>{
    constructor(result: ErrorResponse) : super(result) {}

    constructor() {}
}
