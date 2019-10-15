package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.novel.read.model.db.CollBookBean

class DeleteTaskEvent(var collBook: CollBookBean) : BaseEvent<Any>()
