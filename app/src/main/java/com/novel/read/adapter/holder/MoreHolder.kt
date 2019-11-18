package com.novel.read.adapter.holder

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.novel.read.R

/**
 * @author: LiJun 390057892@qq.com
 * @date: 2018/4/4 9:28
 */

class MoreHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val mProgressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    private val mTvName: TextView = itemView.findViewById(R.id.tv_name)
    private val mTvEnd: TextView = itemView.findViewById(R.id.tv_end)

    fun bindModule(loadMore: Boolean) {
        if (loadMore) {
            mTvName.visibility = View.VISIBLE
            mProgressBar.visibility = View.VISIBLE
            mTvEnd.visibility = View.GONE
        } else {
            mTvName.visibility = View.GONE
            mProgressBar.visibility = View.GONE
            mTvEnd.visibility = View.VISIBLE
        }
    }

}
