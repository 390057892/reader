package com.novel.read.ui.main.mail.pick

import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.data.model.HotRank
import com.novel.read.databinding.ItemHotItemBinding
import com.novel.read.lib.ATH
import com.novel.read.ui.info.BookInfoActivity
import org.jetbrains.anko.sdk27.listeners.onClick

class HotAdapter :BaseBindingAdapter<HotRank,ItemHotItemBinding>() {

    override fun convert(holder: VBViewHolder<ItemHotItemBinding>, item: HotRank) {
        holder.vb.run {
            ivCover.load(item.coverImageUrl, item.getBName(), item.getBAuthor())
            tvName.text = item.getBName()
        }
        holder.itemView.run {
            ATH.applyBackgroundTint(this)
            onClick {
                BookInfoActivity.actionBookInfo(context,item.bookId,item.bookTypeId)
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemHotItemBinding {
        return ItemHotItemBinding.inflate(inflater,parent,false)
    }
}