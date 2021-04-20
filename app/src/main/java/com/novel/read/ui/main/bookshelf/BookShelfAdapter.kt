package com.novel.read.ui.main.bookshelf

import android.os.Bundle
import android.view.ViewGroup
import com.chad.library.adapter.base.entity.SectionEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.getViewBinding
import com.novel.read.base.withBinding
import com.novel.read.constant.BookType
import com.novel.read.data.db.entity.Book
import com.novel.read.databinding.ItemBookshelfListBinding
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.*

class BookShelfAdapter(private val callBack: CallBack) : BaseBookAdapter(R.layout.item_bookshelf_list) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewHolder = super.onCreateDefViewHolder(parent, viewType)
        return viewHolder.withBinding(ItemBookshelfListBinding::bind)
    }

    override fun convert(holder: BaseViewHolder, item: Book, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        val bundle = payloads.getOrNull(0) as? Bundle

        holder.getViewBinding<ItemBookshelfListBinding>().apply {
            if (bundle == null) {
                tvName.text = item.bookName
                tvAuthor.text = item.authorPenname
                tvRead.text = item.durChapterTitle
                tvLast.text = item.keyWord
                ivCover.load(item.coverImageUrl, item.bookName, item.authorPenname)
                upRefresh(this, item)
            } else {
                tvRead.text = item.durChapterTitle
                tvLast.text = item.keyWord
                bundle.keySet().forEach {
                    when (it) {
                        "name" -> tvName.text = item.bookName
                        "author" -> tvAuthor.text = item.authorPenname
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
        holder.getViewBinding<ItemBookshelfListBinding>().apply {
            tvName.text = item.bookName
            tvAuthor.text = item.authorPenname
            tvRead.text = item.durChapterTitle
            tvLast.text = item.keyWord
            ivCover.load(item.coverImageUrl, item.bookName, item.authorPenname)
            upRefresh(this, item)
        }
    }


    private fun upRefresh(holder: ItemBookshelfListBinding, item: Book) {
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