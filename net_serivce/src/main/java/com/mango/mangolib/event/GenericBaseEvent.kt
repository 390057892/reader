package com.mango.mangolib.event

import com.mango.mangolib.http.ErrorResponse

/**
 * create by zlj on 2019/10/12
 * describe:
 */
class GenericBaseEvent(er: ErrorResponse) : BaseEvent<Any>() {

    init {
        this.er = er
    }
}