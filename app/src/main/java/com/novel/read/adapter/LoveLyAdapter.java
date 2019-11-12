package com.novel.read.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.read.R;
import com.novel.read.activity.NovelBookDetailActivity;
import com.novel.read.constants.Constant;
import com.novel.read.model.protocol.RecommendBookResp;
import com.novel.read.utlis.GlideImageLoader;

import java.util.List;

/**
 * 猜你喜欢adapter
 */
public class LoveLyAdapter extends RecyclerView.Adapter {

    private List<RecommendBookResp.BookBean> mList;
    private Context mContext;

    public LoveLyAdapter(List<RecommendBookResp.BookBean> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext==null){
            mContext = viewGroup.getContext();
        }
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_lovely, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolder) {
            RecommendBookResp.BookBean bookBean = mList.get(i);
            GlideImageLoader.INSTANCE.displayCornerImage(mContext, bookBean.getCover(), ((ViewHolder) viewHolder).mIvBook);
            ((ViewHolder) viewHolder).mTvBookName.setText(bookBean.getTitle());
            ((ViewHolder) viewHolder).mTvBookAuthor.setText(mContext.getString(R.string.author_zhu,bookBean.getAuthor()));
            ((ViewHolder) viewHolder).mTvDescription.setText(bookBean.getDescription());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, NovelBookDetailActivity.class);
                    intent.putExtra(Constant.Bundle.BookId, bookBean.getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mIvBook;
        TextView mTvBookName;
        TextView mTvBookAuthor;
        TextView mTvDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvBook = itemView.findViewById(R.id.iv_book);
            mTvBookName = itemView.findViewById(R.id.tv_book_name);
            mTvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            mTvDescription = itemView.findViewById(R.id.tv_book_description);
        }
    }
}
