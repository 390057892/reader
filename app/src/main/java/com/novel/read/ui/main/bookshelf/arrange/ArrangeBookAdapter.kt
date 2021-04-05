package com.novel.read.ui.main.bookshelf.arrange

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.db.entity.Book
import com.novel.read.utils.ext.backgroundColor
import kotlinx.android.synthetic.main.item_arrange_book.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.listeners.onClick

class ArrangeBookAdapter(val callBack: CallBack) : BaseQuickAdapter<Book, BaseViewHolder>(R.layout.item_arrange_book) {
    override fun convert(holder: BaseViewHolder, item: Book) {
        holder.itemView.run {
            backgroundColor = context.backgroundColor
            tv_name.text = item.bookName
            tv_author.text = item.authorPenname
            tv_author.visibility = if (item.authorPenname.isEmpty()) View.GONE else View.VISIBLE
//            checkbox.isChecked = selectedBooks.contains(item)
            iv_book.load(item.coverImageUrl, item.bookName, item.authorPenname)
            tv_delete.onClick {
                getItem(holder.layoutPosition)?.let {
                    callBack.deleteBook(it)
                }
            }
        }
    }


    interface CallBack {
        fun upSelectCount()
        fun updateBook(vararg book: Book)
        fun deleteBook(book: Book)
    }
}