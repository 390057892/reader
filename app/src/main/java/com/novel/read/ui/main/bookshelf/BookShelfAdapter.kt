package com.novel.read.ui.main.bookshelf

import android.os.Bundle
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.constant.BookType
import com.novel.read.data.db.entity.Book
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.item_bookshelf_list.view.*
import kotlinx.android.synthetic.main.item_bookshelf_list.view.bv_unread
import kotlinx.android.synthetic.main.item_bookshelf_list.view.iv_cover
import kotlinx.android.synthetic.main.item_bookshelf_list.view.rl_loading
import kotlinx.android.synthetic.main.item_bookshelf_list.view.tv_name

class BookShelfAdapter(private val callBack: CallBack) : BaseBookAdapter(R.layout.item_bookshelf_list) {


    override fun convert(holder: BaseViewHolder, item: Book, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        val bundle = payloads.getOrNull(0) as? Bundle
        holder.itemView.run {
            ATH.applyBackgroundTint(this)
            if (bundle == null) {
                tv_name.text = item.bookName
                tv_author.text = item.authorPenname
                tv_read.text = item.durChapterTitle
                tv_last.text = item.keyWord
                iv_cover.load(item.coverImageUrl, item.bookName, item.authorPenname)
                upRefresh(holder, item)
            } else {
                tv_read.text = item.durChapterTitle
                tv_last.text = item.keyWord
                bundle.keySet().forEach {
                    when (it) {
                        "name" -> tv_name.text = item.bookName
                        "author" -> tv_author.text = item.authorPenname
                        "cover" -> iv_cover.load(
                            item.coverImageUrl,
                            item.bookName,
                            item.authorPenname
                        )
                        "refresh" -> upRefresh(holder, item)
                    }
                }
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: Book) {
        holder.itemView.run {
            ATH.applyBackgroundTint(this)
            tv_name.text = item.bookName
            tv_author.text = item.authorPenname
            tv_read.text = item.durChapterTitle
            tv_last.text = item.keyWord
            iv_cover.load(item.coverImageUrl, item.bookName, item.authorPenname)
            upRefresh(holder, item)
        }
    }


    private fun upRefresh(holder: BaseViewHolder, item: Book) {
        if (item.origin != BookType.local && callBack.isUpdate(item.bookId)) {
            holder.itemView.bv_unread.invisible()
            holder.itemView.rl_loading.show()
        } else {
            holder.itemView.rl_loading.hide()
            holder.itemView.bv_unread.setBadgeCount(0)
            holder.itemView.bv_unread.setHighlight(true)
        }
    }

}