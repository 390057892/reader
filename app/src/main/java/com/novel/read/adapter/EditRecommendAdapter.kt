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
 * create by zlj on 2019/6/19
 * describe:
 */
class EditRecommendAdapter(private val mList: List<RecommendListResp.ListBean>) : RecyclerView.Adapter<EditRecommendAdapter.ViewHolder>() {

    private var mContext: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        if (mContext == null) {
            mContext = viewGroup.context
        }
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.rlv_edit_recommend_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val listBean = mList[i]
        viewHolder.mTvBookName.text = listBean.book_title
        viewHolder.mTvAuthor.text = listBean.author
        viewHolder.mTvDescription.text = listBean.description
        viewHolder.mTvHumanNum.text = listBean.getHot()
        viewHolder.mTvLoveNum.text = listBean.getLike()
        GlideImageLoader.displayCornerImage(mContext!!, listBean.book_cover!!, viewHolder.mIvBook)
        viewHolder.itemView.setOnClickListener {
            val intent = Intent(mContext, NovelBookDetailActivity::class.java)
            intent.putExtra(Constant.Bundle.BookId, listBean.book_id)
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
        var mTvDescription: TextView = itemView.findViewById(R.id.tv_book_description)
        var mTvHumanNum: TextView = itemView.findViewById(R.id.tv_human_num)
        var mTvLoveNum: TextView = itemView.findViewById(R.id.tv_love_look_num)
    }
}
