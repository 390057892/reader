package com.novel.read.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.novel.read.R
import com.novel.read.model.db.BookSignTable
import com.novel.read.model.protocol.MarkResp

class MarkAdapter(private val mList: List<BookSignTable>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null
    var edit: Boolean = false
        set(edit) {
            field = edit
            notifyDataSetChanged()
        }

    val selectList: String
        get() {
            val signs = StringBuilder()
            for (i in mList.indices) {
                if (mList[i].edit) {
                    if (signs.toString() == "") {
                        signs.append(mList[i].articleId)
                    } else {
                        signs.append(",").append(mList[i].articleId)
                    }
                }
            }
            return signs.toString()
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (mContext == null) {
            mContext = viewGroup.context
        }
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_mark, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is ViewHolder) {
            if (this.edit) {
                viewHolder.mCheck.visibility = View.VISIBLE
                viewHolder.mCheck.setOnCheckedChangeListener { compoundButton, b ->
                    mList[i].edit = b
                }
            } else {
                viewHolder.mCheck.visibility = View.GONE
            }
            viewHolder.mTvMark.text = mList[i].content
            viewHolder.mCheck.isChecked = mList[i].edit
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTvMark: TextView = itemView.findViewById(R.id.tvMarkItem)
        var mCheck: CheckBox = itemView.findViewById(R.id.checkbox)
    }
}
