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
import com.novel.read.model.protocol.RecommendBookResp
import com.novel.read.utlis.GlideImageLoader

/**
 * 猜你喜欢adapter
 */
class LoveLyAdapter(private val mList: List<RecommendBookResp.BookBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (mContext == null) {
            mContext = viewGroup.context
        }
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_lovely, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is ViewHolder) {
            val bookBean = mList[i]
            GlideImageLoader.displayCornerImage(mContext!!, bookBean.cover!!, viewHolder.mIvBook)
            viewHolder.mTvBookName.text = bookBean.title
            viewHolder.mTvBookAuthor.text =
                mContext!!.getString(R.string.author_zhu, bookBean.author)
            viewHolder.mTvDescription.text = bookBean.description
            viewHolder.itemView.setOnClickListener {
                val intent = Intent(mContext, NovelBookDetailActivity::class.java)
                intent.putExtra(Constant.Bundle.BookId, bookBean.id)
                mContext!!.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mIvBook: ImageView = itemView.findViewById(R.id.iv_book)
        var mTvBookName: TextView = itemView.findViewById(R.id.tv_book_name)
        var mTvBookAuthor: TextView = itemView.findViewById(R.id.tv_book_author)
        var mTvDescription: TextView = itemView.findViewById(R.id.tv_book_description)
    }
}
