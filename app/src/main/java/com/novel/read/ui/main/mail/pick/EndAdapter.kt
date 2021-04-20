package com.novel.read.ui.main.mail.pick

import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.data.model.EndRank
import com.novel.read.databinding.ItemEndItemBinding
import com.novel.read.lib.ATH
import com.novel.read.ui.info.BookInfoActivity
import org.jetbrains.anko.sdk27.listeners.onClick

class EndAdapter :BaseBindingAdapter<EndRank,ItemEndItemBinding>() {

    override fun convert(holder: VBViewHolder<ItemEndItemBinding>, item: EndRank) {
        holder.vb.run {
            ivCover.load(item.coverImageUrl, item.getBBookName(), item.getBAuthorName())
            tvName.text = item.getBBookName()
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
    ): ItemEndItemBinding {
        return ItemEndItemBinding.inflate(inflater,parent,false)
    }

}