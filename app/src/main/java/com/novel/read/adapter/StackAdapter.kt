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
import com.novel.read.activity.NovelBookTypeListActivity
import com.novel.read.constants.Constant
import com.novel.read.model.protocol.CategoryTypeResp
import com.novel.read.utlis.GlideImageLoader

class StackAdapter(private val mList: List<CategoryTypeResp.CategoryBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (mContext == null) {
            mContext = viewGroup.context
        }
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_book_type, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is ViewHolder) {

            var input = mList[i].title
            val regex = "(.{2})"
            input = input.replace(regex.toRegex(), "$1\n")
            viewHolder.mTvType.text = input
            viewHolder.itemView.setOnClickListener { view ->
                val intent = Intent(mContext, NovelBookTypeListActivity::class.java)
                intent.putExtra(Constant.Bundle.CategoryId, mList[i].id.toString())
                intent.putExtra(Constant.Bundle.mTitle, mList[i].title)
                mContext!!.startActivity(intent)
            }
            GlideImageLoader.displayCornerImage(
                mContext!!,
                mList[i].cover!!,
                viewHolder.mIvType,
                R.drawable.ic_type_default
            )
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mIvType: ImageView = itemView.findViewById(R.id.iv_book)
        var mTvType: TextView = itemView.findViewById(R.id.tv_book_name)

    }
}
