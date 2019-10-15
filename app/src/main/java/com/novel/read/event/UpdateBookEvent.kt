package com.novel.read.event

import com.mango.mangolib.event.BaseEvent

class UpdateBookEvent : BaseEvent<String> {

    constructor(result: String) : super(result) {}

    constructor() {}
}
