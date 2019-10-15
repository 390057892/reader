package com.novel.read.event

import com.mango.mangolib.event.BaseEvent

class ErrorChapterEvent : BaseEvent<Any> {
    constructor(result: Any) : super(result)

    constructor()
}
