package com.novel.read.ui.chapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.db.entity.Bookmark
import kotlinx.android.synthetic.main.item_bookmark.view.*

class BookMarkAdapter :BaseQuickAdapter<Bookmark,BaseViewHolder>(R.layout.item_bookmark) {

    override fun convert(holder: BaseViewHolder, item: Bookmark) {
        holder.itemView.run {
            tv_chapter_name.text = item.chapterName
            tv_content.text = item.content
        }

    }

}