package com.novel.read.ui.main.mail.pick

import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.getViewBinding
import com.novel.read.base.withBinding
import com.novel.read.constant.LayoutType
import com.novel.read.data.model.RecommendRank
import com.novel.read.databinding.ItemRecommentFirstItemBinding
import com.novel.read.databinding.ItemRecommentItemBinding
import com.novel.read.lib.ATH
import com.novel.read.ui.info.BookInfoActivity
import org.jetbrains.anko.sdk27.listeners.onClick

class RecommendAdapter : BaseMultiItemQuickAdapter<RecommendRank, BaseViewHolder>() {

    init {
        addItemType(LayoutType.RECOMMEND, R.layout.item_recomment_item)
        addItemType(LayoutType.HEAD, R.layout.item_recomment_first_item)
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewHolder = super.onCreateDefViewHolder(parent, viewType)
        return when (viewType) {
            LayoutType.RECOMMEND -> viewHolder.withBinding(ItemRecommentItemBinding::bind)
            LayoutType.HEAD -> viewHolder.withBinding(ItemRecommentFirstItemBinding::bind)
            else -> throw IllegalStateException()
        }
    }

    override fun convert(holder: BaseViewHolder, item: RecommendRank) {
        when (item.itemType) {
            LayoutType.HEAD -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                }
                holder.getViewBinding<ItemRecommentFirstItemBinding>().apply{
                    ivCover.load(item.coverImageUrl, item.getBBookName(), item.getBAuthorName())
                    tvName.text = item.getBBookName()
                }
            }
            LayoutType.RECOMMEND -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)

                    onClick {
                        BookInfoActivity.actionBookInfo(context,item.bookId,item.bookTypeId)
                    }
                }
                holder.getViewBinding<ItemRecommentItemBinding>().apply{
                    ivCover.load(item.coverImageUrl, item.getBBookName(), item.getBAuthorName())
                    tvName.text = item.getBBookName()
                }
            }
        }
    }

}