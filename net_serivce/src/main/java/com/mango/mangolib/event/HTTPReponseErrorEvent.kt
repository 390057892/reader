package com.mango.mangolib.event

import com.mango.mangolib.http.ErrorResponse

/**
 * create by zlj on 2019/10/12
 * describe:
 */
class HTTPReponseErrorEvent : OttoEventInterface {

    var errorResponse: ErrorResponse? = null

    constructor(response: ErrorResponse) {
        this.errorResponse = response
    }

}