package com.novel.read.data.model

data class GoodsResp(
    val bookCommodityClass: Int,
    val bookCommodityId: Long,
    val commodityCode: String,
    val commodityCurrentValuation: Int,
    val commodityName: String,
    val commodityValuation: Int,
    val createTime: Long,
    val discount: Int,
    val remark: String,
    val thirdCommodityNumber: String,
    val validFlag: Int
)