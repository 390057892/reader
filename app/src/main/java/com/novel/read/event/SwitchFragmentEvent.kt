package com.novel.read.event

import com.mango.mangolib.event.BaseEvent

class SwitchFragmentEvent : BaseEvent<Any> {

    constructor(result: Any) : super(result) {}

    constructor() {}
}
