package com.novel.read.model.protocol

import com.novel.read.base.MyApp
import com.novel.read.utlis.StringUtils

import java.io.Serializable
import java.util.ArrayList

/**
 * create by zlj on 2019/6/19
 * describe:
 */
class RecommendListResp : Serializable {


    var list: List<ListBean> = ArrayList()

    class ListBean {
        /**
         * id : 5
         * book_id : 10
         * type : 1
         * gender : 1
         * sort : 10
         * create_time : 1560129944
         * update_time : 1560129944
         * delete_time : null
         * book_title : 重生之末世宝典
         * book_cover : http://api.duoduvip.com/uploads/nocover.jpg
         */

        var id: Int = 0
        var book_id: Int = 0
        var type: Int = 0
        var gender: Int = 0
        var sort: Int = 0
        var create_time: Int = 0
        var update_time: Int = 0
        var delete_time: Any? = null
        var book_title: String? = null
            get() = if (field == null) "" else StringUtils.convertCC(field!!)
        var book_cover: String? = null
            get() = if (field == null) "" else field
        var author: String? = null
            get() = if (field == null) "" else StringUtils.convertCC(field!!)
        var description: String? = null
            get() = if (field == null) "" else StringUtils.convertCC(StringUtils.delete160(field!!))
        private var hot: Int = 0
        private var like: Int = 0

        fun getHot(): String {
            return hot.toString()
        }

        fun setHot(hot: Int) {
            this.hot = hot
        }

        fun getLike(): String {
            return "$like%"
        }

        fun setLike(like: Int) {
            this.like = like
        }
    }
}
