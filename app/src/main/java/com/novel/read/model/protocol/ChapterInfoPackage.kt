package com.novel.read.model.protocol

import com.novel.read.model.db.ChapterInfoBean

import java.io.Serializable

class ChapterInfoPackage : Serializable {

    var article: List<ChapterInfoBean> = ArrayList()


}
