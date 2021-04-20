package com.novel.read.ui.main.mail.pick

import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.data.model.ClickRank
import com.novel.read.databinding.ItemClickItemBinding
import com.novel.read.ui.info.BookInfoActivity
import org.jetbrains.anko.sdk27.listeners.onClick

class ClickAdapter : BaseBindingAdapter<ClickRank, ItemClickItemBinding>() {

    override fun convert(holder: VBViewHolder<ItemClickItemBinding>, item: ClickRank) {
        holder.vb.run {
            ivCover.load(item.coverImageUrl, item.getBBookName(), item.getBAuthorName())
            tvName.text = item.getBBookName()
            tvCount.text = context.getString(R.string.book_word, item.wordCount / 10000)
        }
        holder.itemView.onClick {
            BookInfoActivity.actionBookInfo(context, item.bookId, item.bookTypeId)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemClickItemBinding {
        return ItemClickItemBinding.inflate(inflater, parent, false)
    }

}