package com.novel.read.ui.info

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.model.BookListResp
import com.novel.read.lib.ATH
import kotlinx.android.synthetic.main.item_hot_item.view.*
import org.jetbrains.anko.sdk27.listeners.onClick

class BookInfoAdapter :BaseQuickAdapter<BookListResp,BaseViewHolder>(R.layout.item_hot_item) {

    override fun convert(holder: BaseViewHolder, item: BookListResp) {
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