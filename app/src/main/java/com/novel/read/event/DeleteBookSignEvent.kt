package com.novel.read.event

import com.mango.mangolib.event.BaseEvent
import com.mango.mangolib.http.ErrorResponse

class DeleteBookSignEvent(result: ErrorResponse) : BaseEvent<ErrorResponse>(result)
