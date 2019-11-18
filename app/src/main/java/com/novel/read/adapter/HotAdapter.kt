package com.novel.read.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.novel.read.R
import com.novel.read.constants.Constant

/**
 * create by 赵利君 on 2019/6/17
 * describe:
 */
class HotAdapter(private val mList: List<String>) : RecyclerView.Adapter<HotAdapter.ViewHolder>() {
    private var mContext: Context? = null
    private lateinit var mClickListener: OnItemClickListener

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        if (mContext == null) {
            mContext = viewGroup.context
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_label, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.mTvLabel.text = mList[i]
        viewHolder.mTvLabel.setBackgroundColor(Constant.tagColors[i])
        viewHolder.itemView.setOnClickListener { view -> mClickListener.onItemClick(view, i) }

    }

    override fun getItemCount(): Int {
        return if (mList.size > 8) {
            8
        } else mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTvLabel: TextView = itemView.findViewById(R.id.tv_label)
    }

    fun setOnItemClickListener(mListener: OnItemClickListener) {
        this.mClickListener = mListener
    }


    interface OnItemClickListener {
        fun onItemClick(view: View, pos: Int)
    }
}