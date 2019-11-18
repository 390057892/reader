package com.novel.read.adapter

import android.content.Context
import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.novel.read.R
import com.novel.read.activity.NovelBookDetailActivity
import com.novel.read.constants.Constant
import com.novel.read.model.protocol.RecommendListResp
import com.novel.read.utlis.GlideImageLoader

/**
 * create by zlj on 2019/6/20
 * describe:
 */
class RankAdapter(private val mList: List<RecommendListResp.ListBean>) : RecyclerView.Adapter<RankAdapter.ViewHolder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        if (mContext == null) {
            mContext = viewGroup.context
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.rlv_human_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.mTvBookName.text = mList[i].book_title
        viewHolder.mTvAuthor.text = mList[i].author
        GlideImageLoader.displayCornerImage(mContext!!, mList[i].book_cover!!, viewHolder.mIvBook)
        viewHolder.itemView.setOnClickListener {
            val intent = Intent(mContext, NovelBookDetailActivity::class.java)
            intent.putExtra(Constant.Bundle.BookId, mList[i].book_id)
            mContext!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mIvBook: ImageView = itemView.findViewById(R.id.iv_book)
        var mTvBookName: TextView = itemView.findViewById(R.id.tv_book_name)
        var mTvAuthor: TextView = itemView.findViewById(R.id.tv_book_author)

    }
}
