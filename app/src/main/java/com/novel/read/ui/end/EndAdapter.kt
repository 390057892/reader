package com.novel.read.ui.end

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.model.BookListResp
import com.novel.read.ui.info.BookInfoActivity
import kotlinx.android.synthetic.main.item_book_common.view.*
import org.jetbrains.anko.sdk27.listeners.onClick

class EndAdapter :
    BaseQuickAdapter<BookListResp, BaseViewHolder>(R.layout.item_book_common), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: BookListResp) {
        holder.itemView.run {
            iv_cover.load(item.coverImageUrl, item.getBBookName(), item.getBAuthorName())
            tv_book_name.text = item.getBBookName()
            tv_book_description.text = item.getBIntroduction()
            tv_book_author.text = item.getBAuthorName()
            tv_word.text = context.getString(R.string.book_word, item.wordCount / 10000)
            tv_category.text = item.getBCategoryName()
            onClick {
                BookInfoActivity.actionBookInfo(context,item.bookId,item.bookTypeId)
            }
        }
    }
}