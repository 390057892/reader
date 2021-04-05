package com.novel.read.ui.main.mail.pick

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.constant.LayoutType
import com.novel.read.data.model.RecommendRank
import com.novel.read.lib.ATH
import com.novel.read.ui.info.BookInfoActivity
import kotlinx.android.synthetic.main.item_end_item.view.*
import org.jetbrains.anko.sdk27.listeners.onClick

class RecommendAdapter : BaseMultiItemQuickAdapter<RecommendRank, BaseViewHolder>() {

    init {
        addItemType(LayoutType.RECOMMEND, R.layout.item_recomment_item)
        addItemType(LayoutType.HEAD, R.layout.item_recomment_first_item)
    }

    override fun convert(holder: BaseViewHolder, item: RecommendRank) {
        when (item.itemType) {
            LayoutType.HEAD -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                    iv_cover.load(item.coverImageUrl, item.getBBookName(), item.getBAuthorName())
                    tv_name.text = item.getBBookName()
                }
            }
            LayoutType.RECOMMEND -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                    iv_cover.load(item.coverImageUrl, item.getBBookName(), item.getBAuthorName())
                    tv_name.text = item.getBBookName()
                    onClick {
                        BookInfoActivity.actionBookInfo(context,item.bookId,item.bookTypeId)
                    }
                }
            }
        }
    }

}