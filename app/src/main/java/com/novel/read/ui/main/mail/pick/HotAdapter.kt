package com.novel.read.ui.main.mail.pick

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.model.HotRank
import com.novel.read.lib.ATH
import com.novel.read.ui.info.BookInfoActivity
import kotlinx.android.synthetic.main.item_hot_item.view.*
import org.jetbrains.anko.sdk27.listeners.onClick

class HotAdapter :BaseQuickAdapter<HotRank,BaseViewHolder>(R.layout.item_hot_item) {

    override fun convert(holder: BaseViewHolder, item: HotRank) {
        holder.itemView.run {
            ATH.applyBackgroundTint(this)
            iv_cover.load(item.coverImageUrl, item.getBName(), item.getBAuthor())
            tv_name.text = item.getBName()
            onClick {
                BookInfoActivity.actionBookInfo(context,item.bookId,item.bookTypeId)
            }
        }
    }
}