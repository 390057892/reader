package com.novel.read.ui.main.bookshelf.arrange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.data.db.entity.Book
import com.novel.read.databinding.ItemArrangeBookBinding
import com.novel.read.utils.ext.backgroundColor
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.listeners.onClick

class ArrangeBookAdapter(val callBack: CallBack) :
    BaseBindingAdapter<Book, ItemArrangeBookBinding>() {

    override fun convert(holder: VBViewHolder<ItemArrangeBookBinding>, item: Book) {
        holder.itemView.backgroundColor = context.backgroundColor
        holder.vb.run {
            tvName.text = item.bookName
            tvAuthor.text = item.authorPenname
            tvAuthor.visibility = if (item.authorPenname.isEmpty()) View.GONE else View.VISIBLE
//            checkbox.isChecked = selectedBooks.contains(item)
            ivBook.load(item.coverImageUrl, item.bookName, item.authorPenname)
            tvDelete.onClick {
                getItem(holder.layoutPosition).let {
                    callBack.deleteBook(it)
                }
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemArrangeBookBinding {
        return ItemArrangeBookBinding.inflate(inflater, parent, false)
    }

    interface CallBack {
        fun upSelectCount()
        fun updateBook(vararg book: Book)
        fun deleteBook(book: Book)
    }

}