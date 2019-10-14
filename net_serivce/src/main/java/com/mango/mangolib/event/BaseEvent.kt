package com.mango.mangolib.event

import android.os.Bundle
import com.mango.mangolib.http.ErrorResponse

/**
 * create by zlj on 2019/10/12
 * describe:
 */
open class BaseEvent<T> : OttoEventInterface {

    var result: T? = null

    open var er: ErrorResponse? = null
        set(er) {
            this.isFail = true
            field = er
        }

    public var isFail = false

    private var extras: Bundle? = null

    constructor(result: T) {
        this.result = result
    }

    constructor() {
        this.extras = Bundle()
    }


}
