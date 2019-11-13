package com.novel.read.model.protocol

/**
 * create by 赵利君 on 2019/6/25
 * describe:
 */
class VersionResp {

    /**
     * version : {"id":2,"version":"1.0.0","size":"5096","content":"正式上线","download":"https://play.google.com/store/apps/details?id=com.freebook.bookreader","coerce":1,"status":1,"push_time":1560060702,"create_time":1560060772,"update_time":1560752842,"delete_time":null}
     */

    var version: VersionBean = VersionBean()

    class VersionBean {
        /**
         * id : 2
         * version : 1.0.0
         * size : 5096
         * content : 正式上线
         * download : https://play.google.com/store/apps/details?id=com.freebook.bookreader
         * coerce : 1
         * status : 1
         * push_time : 1560060702
         * create_time : 1560060772
         * update_time : 1560752842
         * delete_time : null
         */

        var id: Int = 0
        var version: String? = null
        var size: String? = null
        var content: String? = null
        var download: String? = null
        var coerce: Int = 0
        var status: Int = 0
        var push_time: Int = 0
        var create_time: Int = 0
        var update_time: Int = 0
        var delete_time: Any? = null
    }
}
