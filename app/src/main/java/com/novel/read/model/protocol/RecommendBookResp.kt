package com.novel.read.model.protocol


import com.novel.read.base.MyApp
import com.novel.read.utlis.StringUtils

import java.io.Serializable
import java.util.ArrayList

/**
 * create by zlj on 2019/6/18
 * describe:
 */
class RecommendBookResp : Serializable {


    var book: List<BookBean> = ArrayList()

    class BookBean {
        /**
         * id : 139
         * title : 丹师剑宗
         * cover : http://dev.duoduvip.com/uploads/20190611/b81d831d3310041846444dacca57cef9.png
         * description :
         * hot : 83146
         * like : 64
         * author : 伯爵
         * create_time : 1560191131
         */

        var id: Int = 0
        var title: String? = null
            get() = if (field == null) "" else StringUtils.convertCC(field!!)
        var cover: String? = null
        var description: String? = null
            get() = if (field == null) "" else StringUtils.convertCC(StringUtils.delete160(field!!))
        var hot: Int = 0
        var like: Int = 0
        var author: String? = null
            get() = if (field == null) "" else StringUtils.convertCC(field!!)
        var create_time: Int = 0
    }
}
