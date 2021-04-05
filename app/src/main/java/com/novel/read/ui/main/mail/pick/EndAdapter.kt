package com.novel.read.ui.main.mail.pick

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.model.EndRank
import com.novel.read.lib.ATH
import com.novel.read.ui.info.BookInfoActivity
import kotlinx.android.synthetic.main.item_end_item.view.*
import org.jetbrains.anko.sdk27.listeners.onClick

class EndAdapter :BaseQuickAdapter<EndRank,BaseViewHolder>(R.layout.item_end_item) {

    override fun convert(holder: BaseViewHolder, item: EndRank) {
        holder.itemView.run {
            ATH.applyBackgroundTint(this)
            iv_cover.load(item.coverImageUrl, item.getBBookName(), item.getBAuthorName())
            tv_name.text = item.getBBookName()
            onClick {
                BookInfoActivity.actionBookInfo(context,item.bookId,item.bookTypeId)
            }
        }
    }

}