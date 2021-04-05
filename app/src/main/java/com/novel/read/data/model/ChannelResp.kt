package com.novel.read.data.model

import com.chad.library.adapter.base.entity.SectionEntity
import com.novel.read.utils.StringUtils

data class ChannelResp(
    val allType: List<AllType>
)

data class AllType(
    val bookTypeId: Long,
    val categoryName: String,
    val createTime: Long,
    val validFlag: Int,
    val typeImageUrl:String,
    val bookCount :Int
) : SectionEntity {
    override val isHeader: Boolean
        get() = false

    fun getBChannel(): String {
        return StringUtils.convertCC(categoryName)
    }
}