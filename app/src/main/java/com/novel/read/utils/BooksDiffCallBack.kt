package com.novel.read.utils

import androidx.annotation.NonNull
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import com.novel.read.data.db.entity.Book

class BooksDiffCallBack : DiffUtil.ItemCallback<Book>() {
    /**
     * 判断是否是同一个item
     *
     * @param oldItem New data
     * @param newItem old Data
     * @return
     */
    override fun areItemsTheSame(
        @NonNull oldItem: Book,
        @NonNull newItem: Book
    ): Boolean {
        return oldItem.bookId === newItem.bookId
    }

    /**
     * 当是同一个item时，再判断内容是否发生改变
     *
     * @param oldItem New data
     * @param newItem old Data
     * @return
     */
    override fun areContentsTheSame(
        @NonNull oldItem: Book,
        @NonNull newItem: Book
    ): Boolean {
        return (oldItem.bookName.equals(newItem.bookName)
                && oldItem.introduction.equals(newItem.introduction)
                && oldItem.lastUpdateChapterDate.equals(newItem.lastUpdateChapterDate))
    }

    /**
     * 可选实现
     * 如果需要精确修改某一个view中的内容，请实现此方法。
     * 如果不实现此方法，或者返回null，将会直接刷新整个item。
     *
     * @param oldItem Old data
     * @param newItem New data
     * @return Payload info. if return null, the entire item will be refreshed.
     */
    override fun getChangePayload(
        @NonNull oldItem: Book,
        @NonNull newItem: Book
    ): Any? {
        val bundle = bundleOf()
        if (oldItem.bookName != newItem.bookName) {
            bundle.putString("name", newItem.bookName)
        }
        if (oldItem.authorPenname != newItem.authorPenname) {
            bundle.putString("author", newItem.authorPenname)
        }
        if (oldItem.durChapterTitle != newItem.durChapterTitle) {
            bundle.putString("dur", newItem.durChapterTitle)
        }
//        if (oldItem.latestChapterTitle != newItem.latestChapterTitle) {
//            bundle.putString("last", newItem.latestChapterTitle)
//        }
        if (oldItem.coverImageUrl != newItem.coverImageUrl) {
            bundle.putString("cover", newItem.coverImageUrl)
        }
        if (oldItem.lastUpdateChapterDate != newItem.lastUpdateChapterDate
            || oldItem.durChapterTime != newItem.durChapterTime
        ) {
            bundle.putBoolean("refresh", true)
        }
        if (bundle.isEmpty) return null
        return bundle
    }
}