package com.novel.read.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.novel.read.constant.LayoutType

class TypeEntity: MultiItemEntity {
    override val itemType: Int
        get() = LayoutType.OTHER
}