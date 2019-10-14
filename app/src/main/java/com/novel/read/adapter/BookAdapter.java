package com.novel.read.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.read.R;
import com.novel.read.activity.NovelReadActivity;
import com.novel.read.model.db.CollBookBean;
import com.novel.read.utlis.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter {

    private List<CollBookBean> mList;
    private Context mContext;
    private final int VALUE_ITEM = 100; //正常item
    private final int EMPTY_ITEM = 101; //空白item
    protected OnItemClickListener mClickListener;
    private boolean mEdit;

    public BookAdapter(List<CollBookBean> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view;
        if (i == VALUE_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_book, viewGroup, false);
            return new ViewHolder(view);
        } else if (i == EMPTY_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.rlv_empty_add_book, viewGroup, false);
            return new EmptyHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolder) {
            if (mEdit) { //编辑模式
                ((ViewHolder) viewHolder).mIvCheck.setSelected(mList.get(i).isSelect());
                ((ViewHolder) viewHolder).mIvCheck.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).mTvBookName.setText(mList.get(i).getTitle());
                ((ViewHolder) viewHolder).mTvBookAuthor.setText(mList.get(i).getAuthor());
                if (mList.get(i).isUpdate()) {
                    ((ViewHolder) viewHolder).mIvGeng.setVisibility(View.VISIBLE);
                } else {
                    ((ViewHolder) viewHolder).mIvGeng.setVisibility(View.GONE);
                }
                GlideImageLoader.displayCornerImage(mContext, mList.get(i).getCover(), ((ViewHolder) viewHolder).mIvBook);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ViewHolder) viewHolder).mIvCheck.setSelected(!mList.get(i).isSelect());
                        mList.get(i).setSelect(!mList.get(i).isSelect());
                    }
                });
            } else { //正常模式
                ((ViewHolder) viewHolder).mIvCheck.setVisibility(View.GONE);
                if (mList.size() == i) { //最后的条目
                    ((ViewHolder) viewHolder).mTvBookName.setText("");
                    ((ViewHolder) viewHolder).mTvBookAuthor.setText("");
                    ((ViewHolder) viewHolder).mIvBook.setImageResource(R.drawable.ic_book_add);
                    ((ViewHolder) viewHolder).mIvGeng.setVisibility(View.GONE);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mClickListener.onItemClick(view, i);
                        }
                    });
                } else {
                    ((ViewHolder) viewHolder).mTvBookName.setText(mList.get(i).getTitle());
                    ((ViewHolder) viewHolder).mTvBookAuthor.setText(mList.get(i).getAuthor());
                    if (mList.get(i).isUpdate()) {
                        ((ViewHolder) viewHolder).mIvGeng.setVisibility(View.VISIBLE);
                    } else {
                        ((ViewHolder) viewHolder).mIvGeng.setVisibility(View.GONE);
                    }
                    GlideImageLoader.displayCornerImage(mContext, mList.get(i).getCover(), ((ViewHolder) viewHolder).mIvBook);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, NovelReadActivity.class);
                            intent.putExtra(NovelReadActivity.EXTRA_IS_COLLECTED, true);
                            intent.putExtra(NovelReadActivity.EXTRA_COLL_BOOK, mList.get(i));
                            mContext.startActivity(intent);
                        }
                    });
                }
            }
        } else if (viewHolder instanceof EmptyHolder) { //空条目
            ((EmptyHolder) viewHolder).mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(view, i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size() == 0) {
            return 1;
        }
        if (mEdit) {
            return mList.size();
        } else {
            return mList.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList == null || mList.size() == 0) {
            return EMPTY_ITEM;
        } else {
            return VALUE_ITEM;
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvBook;
        TextView mTvBookName, mTvBookAuthor;
        ImageView mIvCheck;
        ImageView mIvGeng;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvBook = itemView.findViewById(R.id.iv_book);
            mTvBookName = itemView.findViewById(R.id.tv_book_name);
            mTvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            mIvCheck = itemView.findViewById(R.id.iv_check);
            mIvGeng = itemView.findViewById(R.id.iv_geng);
        }
    }

    static class EmptyHolder extends RecyclerView.ViewHolder {
        Button mBtnAdd;

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
            mBtnAdd = itemView.findViewById(R.id.btn_add);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mClickListener = mListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    public void setEdit(boolean edit) {  //开启编辑模式
        mEdit = edit;
        notifyDataSetChanged();
    }

    public List<CollBookBean> getSelectList() {
        List<CollBookBean> collBookBeans = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isSelect()) {
                collBookBeans.add(mList.get(i));
            }
        }
        return collBookBeans;
    }

}
