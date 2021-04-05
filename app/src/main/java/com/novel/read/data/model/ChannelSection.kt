package com.novel.read.data.model

import com.chad.library.adapter.base.entity.SectionEntity

data class ChannelSection(
    override val isHeader: Boolean,
    val obj: Any
) : SectionEntity