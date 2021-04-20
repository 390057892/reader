package com.novel.read.ui.chapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.data.db.entity.Bookmark
import com.novel.read.databinding.ItemBookmarkBinding

class BookMarkAdapter :BaseBindingAdapter<Bookmark,ItemBookmarkBinding>() {

    override fun convert(holder: VBViewHolder<ItemBookmarkBinding>, item: Bookmark) {
        holder.vb.run {
            tvChapterName.text = item.chapterName
            tvContent.text = item.content
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemBookmarkBinding {
        return ItemBookmarkBinding.inflate(inflater, parent, false)
    }

}