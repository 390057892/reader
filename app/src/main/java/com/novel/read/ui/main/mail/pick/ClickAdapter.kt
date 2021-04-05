package com.novel.read.ui.main.mail.pick

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.model.ClickRank
import com.novel.read.ui.info.BookInfoActivity
import kotlinx.android.synthetic.main.item_click_item.view.*
import org.jetbrains.anko.sdk27.listeners.onClick

class ClickAdapter :BaseQuickAdapter<ClickRank,BaseViewHolder>(R.layout.item_click_item) {

    override fun convert(holder: BaseViewHolder, item: ClickRank) {
        holder.itemView.run {
            iv_cover.load(item.coverImageUrl, item.getBBookName(), item.getBAuthorName())
            tv_name.text = item.getBBookName()
            tv_count.text =  context.getString(R.string.book_word, item.wordCount / 10000)
            onClick {
                BookInfoActivity.actionBookInfo(context,item.bookId,item.bookTypeId)
            }
        }
    }

}