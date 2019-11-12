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
import com.novel.read.activity.NovelBookTypeListActivity;
import com.novel.read.constants.Constant;
import com.novel.read.model.protocol.CategoryTypeResp;
import com.novel.read.utlis.GlideImageLoader;

import java.util.List;

public class StackAdapter extends RecyclerView.Adapter {

    private List<CategoryTypeResp.CategoryBean> mList;
    private Context mContext;

    public StackAdapter(List<CategoryTypeResp.CategoryBean> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_book_type, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof ViewHolder) {

            String input = mList.get(i).getTitle();
            String regex = "(.{2})";
            input = input.replaceAll(regex, "$1\n");
            ((ViewHolder) viewHolder).mTvType.setText(input);
            viewHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, NovelBookTypeListActivity.class);
                intent.putExtra(Constant.Bundle.CategoryId, String.valueOf(mList.get(i).getId()));
                intent.putExtra(Constant.Bundle.mTitle, mList.get(i).getTitle());
                mContext.startActivity(intent);
            });
            GlideImageLoader.INSTANCE.displayCornerImage(mContext, mList.get(i).getCover(), ((ViewHolder) viewHolder).mIvType, R.drawable.ic_type_default);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvType;
        TextView mTvType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvType = itemView.findViewById(R.id.iv_book);
            mTvType = itemView.findViewById(R.id.tv_book_name);

        }
    }
}
