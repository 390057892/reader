package com.novel.read.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.read.R;
import com.novel.read.activity.NovelBookDetailActivity;
import com.novel.read.adapter.holder.EmptyHolder;
import com.novel.read.adapter.holder.MoreHolder;
import com.novel.read.constants.Constant;
import com.novel.read.inter.OnLoadMoreListener;
import com.novel.read.model.protocol.SearchResp;
import com.novel.read.utlis.GlideImageLoader;

import java.util.List;
import static com.novel.read.constants.Constant.COMMENT_SIZE;

public class SearchAdapter extends RecyclerView.Adapter {

    private List<SearchResp.BookBean> mList;
    private Context mContext;
    private final int VALUE_ITEM = 100; //正常item
    private final int BOOK_ITEM = 102; //书本item
    private final int EMPTY_ITEM = 101; //空白item
    private boolean book = false;
    protected OnItemClickListener mClickListener;

    private final int PROCESS_ITEM = 103; //加载更多
    private boolean loadingMore;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 1;
    private OnLoadMoreListener mOnLoadMoreListener;

    public SearchAdapter(List<SearchResp.BookBean> mList, RecyclerView recyclerView) {
        this.mList = mList;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager llMangager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = llMangager.getItemCount();
                    lastVisibleItem = llMangager.findLastVisibleItemPosition();
                    if (!loadingMore && totalItemCount == (lastVisibleItem + visibleThreshold) && totalItemCount >= (COMMENT_SIZE)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                    }
                }
            });

        }
    }


    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
    }

    public boolean isLoadingMore() {
        return loadingMore;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view;
        if (i == VALUE_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_search, viewGroup, false);
            return new ViewHolder(view);
        } else if (i == BOOK_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_book_list_search, viewGroup, false);
            return new BookHolder(view);
        } else if (i == EMPTY_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.rlv_empty_view, viewGroup, false);
            return new EmptyHolder(view);
        } else if (i == PROCESS_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.load_more_layout, viewGroup, false);
            return new MoreHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolder) {
            SearchResp.BookBean bookBean = mList.get(i);
            ((ViewHolder) viewHolder).mTvBookName.setText(bookBean.getTitle());
            viewHolder.itemView.setOnClickListener(view -> mClickListener.onItemClick(view, i));
        } else if (viewHolder instanceof BookHolder) {
            SearchResp.BookBean bookBean = mList.get(i);
            ((BookHolder) viewHolder).tvBookName.setText(bookBean.getTitle());
            ((BookHolder) viewHolder).tvBookAuthor.setText(bookBean.getAuthor());
            ((BookHolder) viewHolder).tvBookDescription.setText(bookBean.getDescription());
            GlideImageLoader.INSTANCE.displayCornerImage(mContext, bookBean.getCover(), ((BookHolder) viewHolder).ivBook);
            viewHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, NovelBookDetailActivity.class);
                intent.putExtra(Constant.Bundle.BookId, bookBean.getId());
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size() == 0) {
            return 1;
        }
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mList == null || mList.size() == 0) {
            return EMPTY_ITEM;
        } else {
            if (mList.get(position) == null) {
                return PROCESS_ITEM;
            }
            if (book) {
                return BOOK_ITEM;

            } else {
                return VALUE_ITEM;
            }

        }
    }

    public void setHolderType(boolean book) {
        this.book = book;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvBookName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvBookName = itemView.findViewById(R.id.tv_book_name);
        }
    }

    static class BookHolder extends RecyclerView.ViewHolder {
        ImageView ivBook;
        TextView tvBookName;
        TextView tvBookAuthor;
        TextView tvBookDescription;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            ivBook = itemView.findViewById(R.id.iv_book);
            tvBookName = itemView.findViewById(R.id.tv_book_name);
            tvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            tvBookDescription = itemView.findViewById(R.id.tv_book_description);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mClickListener = mListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }
}
