package com.novel.read.event

import com.mango.mangolib.event.BaseEvent

class FinishChapterEvent : BaseEvent<Any> {
    constructor(result: Any) : super(result)

    constructor()
}
