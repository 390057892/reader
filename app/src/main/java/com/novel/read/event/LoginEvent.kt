package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.UidResp

class LoginEvent : BaseEvent<UidResp> {
    constructor(result: UidResp) : super(result) {}

    constructor() {}
}
