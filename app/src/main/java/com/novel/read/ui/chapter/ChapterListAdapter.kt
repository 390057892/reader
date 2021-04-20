package com.novel.read.ui.chapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.novel.read.R
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.databinding.ItemChapterListBinding
import com.novel.read.help.BookHelp
import com.novel.read.utils.ext.*

class ChapterListAdapter(val callback: Callback) :
    BaseBindingAdapter<BookChapter, ItemChapterListBinding>() {

    val cacheFileNames = hashSetOf<String>()


    override fun convert(holder: VBViewHolder<ItemChapterListBinding>, item: BookChapter) {

        holder.vb.run {
            val isDur = callback.durChapterIndex() == item.chapterIndex
            val cached = callback.isLocalBook
                    || cacheFileNames.contains(BookHelp.formatChapterName(item))
            if (isDur) {
                tvChapterName.setTextColor(context.accentColor)
            } else {
                tvChapterName.setTextColor(context.getCompatColor(R.color.primaryText))
            }
            tvChapterName.text = item.chapterName
            if (!item.updateDate.isNullOrEmpty()) {
                tvTag.text = item.updateDate
                tvTag.visible()
            }
            upHasCache(this, isDur, cached)

        }

        holder.itemView.run {
            setOnClickListener {
                getItem(holder.layoutPosition).let {
                    callback.openChapter(it)
                }
            }
        }

    }

    private fun upHasCache(itemView: ItemChapterListBinding, isDur: Boolean, cached: Boolean) = itemView.run {
        tvChapterName.paint.isFakeBoldText = cached
        ivChecked.setImageResource(R.drawable.ic_outline_cloud_24)
        ivChecked.visible(!cached)
        if (isDur) {
            ivChecked.setImageResource(R.drawable.ic_check)
            ivChecked.visible()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemChapterListBinding {
        return ItemChapterListBinding.inflate(inflater,parent,false)
    }

    interface Callback {
        val isLocalBook: Boolean
        fun openChapter(bookChapter: BookChapter)
        fun durChapterIndex(): Int
    }

}