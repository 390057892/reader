package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.VersionResp

/**
 * create by 赵利君 on 2019/6/25
 * describe:
 */
class VersionEvent : BaseEvent<VersionResp> {
    constructor(result: VersionResp) : super(result) {}

    constructor() {}
}
