package com.novel.read.model.protocol

class MarkResp {

    var sign: List<SignBean>? = null

    class SignBean {
        /**
         * id : 8
         * uid : 1
         * book_id : 1
         * article_id : 1
         * words : 0
         * create_time : 1561450031
         * update_time : 1561450031
         * delete_time : null
         */

        var id: Int = 0
        var uid: Int = 0
        var book_id: Int = 0
        var article_id: Int = 0
        var words: Int = 0
        var create_time: Int = 0
        var update_time: Int = 0
        var delete_time: Any? = null
        var content: String? = null
            get() = if (field == null) "" else field
        var isEdit: Boolean = false
    }
}
