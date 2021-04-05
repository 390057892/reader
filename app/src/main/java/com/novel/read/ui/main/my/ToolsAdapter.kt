package com.novel.read.ui.main.my

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.model.Tools
import com.novel.read.lib.ATH
import kotlinx.android.synthetic.main.item_tools.view.*

class ToolsAdapter : BaseQuickAdapter<Tools, BaseViewHolder>(R.layout.item_tools) {

    override fun convert(holder: BaseViewHolder, item: Tools) {
        holder.itemView.run {
            ATH.applyBackgroundTint(this)
            tv_tools.text = item.title
            iv_tools.setImageResource(item.img)
        }
    }
}