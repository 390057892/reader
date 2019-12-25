package com.novel.read.utlis

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 设置adapter padding
 */
class EvenItemDecoration(private val space: Int, private val column: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        // 每个span分配的间隔大小
        val spanSpace = space * (column +1) / column
        // 列索引
        val colIndex = position % column
        // 列左、右间隙
        outRect.left = space * (colIndex + 1) - spanSpace * colIndex
        outRect.right = spanSpace * (colIndex + 1) - space * (colIndex + 1)
        // 行间距
    }
}