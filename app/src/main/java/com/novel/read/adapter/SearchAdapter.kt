package com.novel.read.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.novel.read.R
import com.novel.read.activity.NovelBookDetailActivity
import com.novel.read.adapter.holder.EmptyHolder
import com.novel.read.adapter.holder.MoreHolder
import com.novel.read.constants.Constant
import com.novel.read.constants.Constant.COMMENT_SIZE
import com.novel.read.inter.OnLoadMoreListener
import com.novel.read.model.protocol.SearchResp
import com.novel.read.utlis.GlideImageLoader

class SearchAdapter(private val mList: List<SearchResp.BookBean>, recyclerView: RecyclerView) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null

    private var book = false
    private var mClickListener: OnItemClickListener? = null

    var isLoadingMore: Boolean = false
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private val visibleThreshold = 1
    private var mOnLoadMoreListener: OnLoadMoreListener? = null

    init {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            val manager = recyclerView.layoutManager as LinearLayoutManager?
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    totalItemCount = manager!!.itemCount
                    lastVisibleItem = manager.findLastVisibleItemPosition()
                    if (!isLoadingMore && totalItemCount == lastVisibleItem + visibleThreshold && totalItemCount >= COMMENT_SIZE) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener!!.onLoadMore()
                        }
                    }
                }
            })

        }
    }

    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (mContext == null) {
            mContext = viewGroup.context
        }
        val view: View
        when (i) {
            VALUE_ITEM -> {
                view = LayoutInflater.from(mContext)
                    .inflate(R.layout.rlv_item_search, viewGroup, false)
                return ViewHolder(view)
            }
            BOOK_ITEM -> {
                view = LayoutInflater.from(mContext)
                    .inflate(R.layout.rlv_item_book_list_search, viewGroup, false)
                return BookHolder(view)
            }
            EMPTY_ITEM -> {
                view =
                    LayoutInflater.from(mContext).inflate(R.layout.rlv_empty_view, viewGroup, false)
                return EmptyHolder(view)
            }
            PROCESS_ITEM -> {
                view = LayoutInflater.from(mContext)
                    .inflate(R.layout.load_more_layout, viewGroup, false)
                return MoreHolder(view)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        when (viewHolder) {
            is ViewHolder -> {
                val bookBean = mList[i]
                viewHolder.mTvBookName.text = bookBean.title
                viewHolder.itemView.setOnClickListener { view ->
                    mClickListener!!.onItemClick(
                        view,
                        i
                    )
                }
            }
            is BookHolder -> {
                val bookBean = mList[i]
                viewHolder.tvBookName.text = bookBean.title
                viewHolder.tvBookAuthor.text = bookBean.author
                viewHolder.tvBookDescription.text = bookBean.description
                GlideImageLoader.displayCornerImage(mContext!!, bookBean.cover!!, viewHolder.ivBook)
                viewHolder.itemView.setOnClickListener { view ->
                    val intent = Intent(mContext, NovelBookDetailActivity::class.java)
                    intent.putExtra(Constant.Bundle.BookId, bookBean.id)
                    mContext!!.startActivity(intent)
                }
            }
            is MoreHolder -> viewHolder.bindModule(isLoadingMore)
        }
    }

    override fun getItemCount(): Int {
        return if (mList.isEmpty()) {
            1
        } else mList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (mList.isNotEmpty() && position == itemCount - 1) {
            return PROCESS_ITEM
        }

        return if (mList.isEmpty()) {
            EMPTY_ITEM
        } else {
            if (book) {
                BOOK_ITEM
            } else {
                VALUE_ITEM
            }
        }
    }

    fun setHolderType(book: Boolean) {
        this.book = book
        notifyDataSetChanged()
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTvBookName: TextView = itemView.findViewById(R.id.tv_book_name)

    }

    internal class BookHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivBook: ImageView = itemView.findViewById(R.id.iv_book)
        var tvBookName: TextView = itemView.findViewById(R.id.tv_book_name)
        var tvBookAuthor: TextView = itemView.findViewById(R.id.tv_book_author)
        var tvBookDescription: TextView = itemView.findViewById(R.id.tv_book_description)

    }

    fun setOnItemClickListener(mListener: OnItemClickListener) {
        this.mClickListener = mListener
    }


    interface OnItemClickListener {
        fun onItemClick(view: View, pos: Int)
    }

    companion object {

        private const val VALUE_ITEM = 100 //正常item

        private const val EMPTY_ITEM = 101 //空白item

        private const val BOOK_ITEM = 102 //书本item

        private const val PROCESS_ITEM = 103
    }

}
