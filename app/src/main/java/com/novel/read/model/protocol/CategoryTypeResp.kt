package com.novel.read.model.protocol

import com.novel.read.base.MyApp
import com.novel.read.utlis.StringUtils

import java.io.Serializable

class CategoryTypeResp : Serializable {


    var category: List<CategoryBean> = arrayListOf()

    class CategoryBean {
        /**
         * id : 1
         * title : 玄幻奇幻
         * cover :
         */

        var id: Int = 0
        var title: String = ""
            get() = StringUtils.convertCC(field)
        var cover: String = ""
    }
}
