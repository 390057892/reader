package com.novel.read.ui.search

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.constant.AppConst
import com.novel.read.utils.StringUtils
import kotlinx.android.synthetic.main.item_label.view.*

class HotAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_label) {

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.itemView.run {
            tv_label.text = StringUtils.convertCC(item)
            tv_label.setBackgroundColor(AppConst.tagColors[holder.adapterPosition % 8])
        }
    }

}