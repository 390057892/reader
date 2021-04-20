package com.novel.read.ui.main.bookshelf

import android.os.Bundle
import android.view.ViewGroup
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.getViewBinding
import com.novel.read.base.withBinding
import com.novel.read.constant.BookType
import com.novel.read.data.db.entity.Book
import com.novel.read.databinding.ItemBookshelfGridBinding
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.invisible

class BooksAdapterGrid(private val callBack: CallBack) :
    BaseBookAdapter(R.layout.item_bookshelf_grid) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewHolder = super.onCreateDefViewHolder(parent, viewType)
        return viewHolder.withBinding(ItemBookshelfGridBinding::bind)
    }

    override fun convert(holder: BaseViewHolder, item: Book, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        val bundle = payloads.getOrNull(0) as? Bundle
        holder.getViewBinding<ItemBookshelfGridBinding>().apply{
            if (bundle == null) {
                tvName.text = item.bookName
                ivCover.load(item.coverImageUrl, item.bookName, item.authorPenname)
                upRefresh(this, item)
            } else {
                bundle.keySet().forEach {
                    when (it) {
                        "name" -> tvName.text = item.bookName
                        "cover" -> ivCover.load(
                            item.coverImageUrl,
                            item.bookName,
                            item.authorPenname
                        )
                        "refresh" -> upRefresh(this, item)
                    }
                }
            }
        }
        holder.itemView.run {
            ATH.applyBackgroundTint(this)
        }
    }

    override fun convert(holder: BaseViewHolder, item: Book) {
        holder.itemView.run {
            ATH.applyBackgroundTint(this)
        }
        holder.getViewBinding<ItemBookshelfGridBinding>().apply{
            tvName.text = item.bookName
            ivCover.load(item.coverImageUrl, item.bookName, item.authorPenname)
            upRefresh(this, item)
        }
    }

    private fun upRefresh(holder: ItemBookshelfGridBinding, item: Book) {
        if (item.origin != BookType.local && callBack.isUpdate(item.bookId)) {
            holder.bvUnread.invisible()
            holder.rlLoading.show()
        } else {
            holder.rlLoading.hide()
            holder.bvUnread.setBadgeCount(0)
            holder.bvUnread.setHighlight(true)
        }
    }
}