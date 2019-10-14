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
import com.novel.read.model.protocol.RecommendListResp;
import com.novel.read.utlis.GlideImageLoader;

import java.util.List;

/**
 * create by 赵利君 on 2019/6/19
 * describe:
 */
public class EditRecommendAdapter extends RecyclerView.Adapter<EditRecommendAdapter.ViewHolder> {

    private List<RecommendListResp.ListBean> mList;
    private Context mContext;
    public EditRecommendAdapter(List<RecommendListResp.ListBean> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext==null){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.rlv_edit_recommend_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        RecommendListResp.ListBean listBean = mList.get(i);
        viewHolder.mTvBookName.setText(listBean.getBook_title());
        viewHolder.mTvAuthor.setText(listBean.getAuthor());
        viewHolder.mTvDescription.setText(listBean.getDescription());
        viewHolder.mTvHumanNum.setText(listBean.getHot());
        viewHolder.mTvLoveNum.setText(listBean.getLike());
        GlideImageLoader.displayCornerImage(mContext,listBean.getBook_cover(),viewHolder.mIvBook);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, NovelBookDetailActivity.class);
                intent.putExtra(Constant.Bundle.BookId, listBean.getBook_id());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mIvBook;
        TextView mTvBookName;
        TextView mTvAuthor;
        TextView mTvDescription;
        TextView mTvHumanNum;
        TextView mTvLoveNum;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvBook = itemView.findViewById(R.id.iv_book);
            mTvBookName = itemView.findViewById(R.id.tv_book_name);
            mTvAuthor = itemView.findViewById(R.id.tv_book_author);
            mTvDescription = itemView.findViewById(R.id.tv_book_description);
            mTvHumanNum = itemView.findViewById(R.id.tv_human_num);
            mTvLoveNum = itemView.findViewById(R.id.tv_love_look_num);
        }
    }
}
