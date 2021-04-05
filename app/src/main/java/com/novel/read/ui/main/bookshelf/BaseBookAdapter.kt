package com.novel.read.ui.main.bookshelf

import androidx.core.os.bundleOf
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.data.db.entity.Book

abstract class BaseBookAdapter(layoutId: Int) : BaseQuickAdapter<Book, BaseViewHolder>(layoutId){


    fun notification(bookId: Long) {
        for (i in 0 until itemCount) {
            getItem(i).let {
                if (it.bookId == bookId) {
                    notifyItemChanged(i, bundleOf(Pair("refresh", null)))
                    return
                }
            }
        }
    }

    interface CallBack {
        fun isUpdate(bookId: Long): Boolean
    }

}