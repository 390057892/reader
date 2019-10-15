package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.protocol.HotSearchResp

/**
 * create by zlj on 2019/6/18
 * describe:
 */
class HotSearchEvent(result: HotSearchResp) : BaseEvent<HotSearchResp>(result)
