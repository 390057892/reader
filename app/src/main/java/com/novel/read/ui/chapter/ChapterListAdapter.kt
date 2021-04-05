package com.novel.read.ui.chapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.help.BookHelp
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.item_chapter_list.view.*

class ChapterListAdapter(val callback: Callback) :
    BaseQuickAdapter<BookChapter, BaseViewHolder>(R.layout.item_chapter_list) {

    val cacheFileNames = hashSetOf<String>()

    override fun convert(holder: BaseViewHolder, item: BookChapter) {
        holder.itemView.run {
            val isDur = callback.durChapterIndex() == item.chapterIndex
            val cached = callback.isLocalBook
                    || cacheFileNames.contains(BookHelp.formatChapterName(item))
            if (isDur) {
                tv_chapter_name.setTextColor(context.accentColor)
            } else {
                tv_chapter_name.setTextColor(context.getCompatColor(R.color.primaryText))
            }
            tv_chapter_name.text = item.chapterName
            if (!item.updateDate.isNullOrEmpty()) {
                tv_tag.text = item.updateDate
                tv_tag.visible()
            }
            upHasCache(this, isDur, cached)

            setOnClickListener {
                getItem(holder.layoutPosition)?.let {
                    callback.openChapter(it)
                }
            }
        }
    }

    private fun upHasCache(itemView: View, isDur: Boolean, cached: Boolean) = itemView.apply {
        tv_chapter_name.paint.isFakeBoldText = cached
        iv_checked.setImageResource(R.drawable.ic_outline_cloud_24)
        iv_checked.visible(!cached)
        if (isDur) {
            iv_checked.setImageResource(R.drawable.ic_check)
            iv_checked.visible()
        }
    }

    interface Callback {
        val isLocalBook: Boolean
        fun openChapter(bookChapter: BookChapter)
        fun durChapterIndex(): Int
    }
}