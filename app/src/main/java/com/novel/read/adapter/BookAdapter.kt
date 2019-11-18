package com.novel.read.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.novel.read.R
import com.novel.read.activity.NovelReadActivity
import com.novel.read.model.db.CollBookBean
import com.novel.read.utlis.GlideImageLoader

import java.util.ArrayList

class BookAdapter(private val mList: List<CollBookBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mContext: Context? = null

    private var mClickListener: OnItemClickListener? = null
    private var mEdit: Boolean = false

    val selectList: List<CollBookBean>
        get() {
            val collBookBeans = ArrayList<CollBookBean>()
            for (i in mList.indices) {
                if (mList[i].isSelect) {
                    collBookBeans.add(mList[i])
                }
            }
            return collBookBeans
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (mContext == null) {
            mContext = viewGroup.context
        }
        val view: View
        if (i == VALUE_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_book, viewGroup, false)
            return ViewHolder(view)
        } else if (i == EMPTY_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.rlv_empty_add_book, viewGroup, false)
            return EmptyHolder(view)
        }
        throw IllegalArgumentException()
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is ViewHolder) {
            if (mEdit) { //编辑模式
                viewHolder.mIvCheck.isSelected = mList[i].isSelect
                viewHolder.mIvCheck.visibility = View.VISIBLE
                viewHolder.mTvBookName.text = mList[i].title
                viewHolder.mTvBookAuthor.text = mList[i].author
                if (mList[i].isUpdate) {
                    viewHolder.mIvGeng.visibility = View.VISIBLE
                } else {
                    viewHolder.mIvGeng.visibility = View.GONE
                }
                GlideImageLoader.displayCornerImage(mContext!!, mList[i].cover, viewHolder.mIvBook)
                viewHolder.itemView.setOnClickListener {
                    viewHolder.mIvCheck.isSelected = !mList[i].isSelect
                    mList[i].isSelect = !mList[i].isSelect
                }
            } else { //正常模式
                viewHolder.mIvCheck.visibility = View.GONE
                if (mList.size == i) { //最后的条目
                    viewHolder.mTvBookName.text = ""
                    viewHolder.mTvBookAuthor.text = ""
                    viewHolder.mIvBook.setImageResource(R.drawable.ic_book_add)
                    viewHolder.mIvGeng.visibility = View.GONE
                    viewHolder.itemView.setOnClickListener { view ->
                        mClickListener!!.onItemClick(
                            view,
                            i
                        )
                    }
                } else {
                    viewHolder.mTvBookName.text = mList[i].title
                    viewHolder.mTvBookAuthor.text = mList[i].author
                    if (mList[i].isUpdate) {
                        viewHolder.mIvGeng.visibility = View.VISIBLE
                    } else {
                        viewHolder.mIvGeng.visibility = View.GONE
                    }
                    GlideImageLoader.displayCornerImage(
                        mContext!!,
                        mList[i].cover,
                        viewHolder.mIvBook
                    )
                    viewHolder.itemView.setOnClickListener {
                        val intent = Intent(mContext, NovelReadActivity::class.java)
                        intent.putExtra(NovelReadActivity.EXTRA_IS_COLLECTED, true)
                        intent.putExtra(NovelReadActivity.EXTRA_COLL_BOOK, mList[i])
                        mContext!!.startActivity(intent)
                    }
                }
            }
        } else if (viewHolder is EmptyHolder) { //空条目
            viewHolder.mBtnAdd.setOnClickListener { view -> mClickListener!!.onItemClick(view, i) }
        }
    }

    override fun getItemCount(): Int {
        if (mList.isEmpty()) {
            return 1
        }
        return if (mEdit) {
            mList.size
        } else {
            mList.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList.isEmpty()) {
            EMPTY_ITEM
        } else {
            VALUE_ITEM
        }

    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mIvBook: ImageView = itemView.findViewById(R.id.iv_book)
        var mTvBookName: TextView = itemView.findViewById(R.id.tv_book_name)
        var mTvBookAuthor: TextView = itemView.findViewById(R.id.tv_book_author)
        var mIvCheck: ImageView = itemView.findViewById(R.id.iv_check)
        var mIvGeng: ImageView = itemView.findViewById(R.id.iv_geng)

    }

    internal class EmptyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mBtnAdd: Button = itemView.findViewById(R.id.btn_add)

    }

    fun setOnItemClickListener(mListener: OnItemClickListener) {
        this.mClickListener = mListener
    }


    interface OnItemClickListener {
        fun onItemClick(view: View, pos: Int)
    }

    fun setEdit(edit: Boolean) {  //开启编辑模式
        mEdit = edit
        notifyDataSetChanged()
    }


    companion object {

        private const val VALUE_ITEM = 100 //正常item

        private const val EMPTY_ITEM = 101 //空白item

    }

}
