package com.novel.read.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.widget.page.PageLoader
import com.novel.read.widget.page.PageStyle

class PageStyleAdapter(val mList: List<Drawable>, private val mPageLoader: PageLoader) :
    RecyclerView.Adapter<PageStyleAdapter.PageHolder>() {
    private var mContext: Context? = null
    private var currentChecked: Int = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PageHolder {
        if (mContext == null) {
            mContext = viewGroup.context
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_read_bg, viewGroup, false)
        return PageHolder(view)
    }

    override fun onBindViewHolder(pageHolder: PageHolder, i: Int) {
        pageHolder.mReadBg.background = mList[i]
        pageHolder.mIvChecked.visibility = View.GONE
        if (currentChecked == i) {
            pageHolder.mIvChecked.visibility = View.VISIBLE
        }
        pageHolder.itemView.setOnClickListener {
            currentChecked = i
            notifyDataSetChanged()
            mPageLoader.setPageStyle(PageStyle.values()[i])
        }
    }

    fun setPageStyleChecked(pageStyle: PageStyle) {
        currentChecked = pageStyle.ordinal
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class PageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mReadBg: View = itemView.findViewById(R.id.read_bg_view)
        val mIvChecked: ImageView = itemView.findViewById(R.id.read_bg_iv_checked)

    }
}
