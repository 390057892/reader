package com.novel.read.event

import com.mango.mangolib.event.BaseEvent

class HideBottomBarEvent(result: Boolean?) : BaseEvent<Boolean>(result!!)
