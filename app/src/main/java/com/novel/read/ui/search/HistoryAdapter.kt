package com.novel.read.ui.search

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.db.entity.SearchHistory
import kotlinx.android.synthetic.main.item_label.view.*

class HistoryAdapter : BaseQuickAdapter<SearchHistory, BaseViewHolder>(R.layout.item_label) {

    override fun convert(holder: BaseViewHolder, item: SearchHistory) {
        holder.itemView.run {
            tv_label.text = item.getBKey()
        }
    }

}