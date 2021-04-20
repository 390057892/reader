package com.novel.read.ui.channel

import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.data.model.BookListResp
import com.novel.read.databinding.ItemBookCommonBinding
import com.novel.read.ui.info.BookInfoActivity
import org.jetbrains.anko.sdk27.listeners.onClick

class ChannelInfoAdapter :
    BaseBindingAdapter<BookListResp, ItemBookCommonBinding>(), LoadMoreModule {

    override fun convert(holder: VBViewHolder<ItemBookCommonBinding>, item: BookListResp) {
        holder.itemView.run {
            onClick {
                BookInfoActivity.actionBookInfo(context,item.bookId,item.bookTypeId)
            }
        }
        holder.vb.run {
            ivCover.load(item.coverImageUrl, item.getBBookName(), item.getBAuthorName())
            tvBookName.text = item.getBBookName()
            tvBookDescription.text = item.getBIntroduction()
            tvBookAuthor.text = item.getBAuthorName()
            tvWord.text = context.getString(R.string.book_word, item.wordCount / 10000)
            tvCategory.text = item.getBCategoryName()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemBookCommonBinding {
        return ItemBookCommonBinding.inflate(inflater,parent,false)
    }
}