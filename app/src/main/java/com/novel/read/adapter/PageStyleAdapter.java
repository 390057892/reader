package com.novel.read.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.widget.page.PageLoader;
import com.novel.read.widget.page.PageStyle;

import java.util.List;

public class PageStyleAdapter extends RecyclerView.Adapter<PageStyleAdapter.PageHolder> {

    private List<Drawable> mList;
    private Context mContext;
    private int currentChecked;
    private PageLoader mPageLoader;
    public PageStyleAdapter(List<Drawable> mList, PageLoader mPageLoader) {
        this.mList = mList;
        this.mPageLoader = mPageLoader;
    }

    @NonNull
    @Override
    public PageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext==null){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_read_bg, viewGroup, false);
        return new PageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageHolder pageHolder, int i) {
        pageHolder.mReadBg.setBackground(mList.get(i));
        pageHolder.mIvChecked.setVisibility(View.GONE);
        if (currentChecked == i){
            pageHolder.mIvChecked.setVisibility(View.VISIBLE);
        }
        pageHolder.itemView.setOnClickListener(view -> {
            currentChecked = i;
            notifyDataSetChanged();
            mPageLoader.setPageStyle(PageStyle.values()[i]);
        });
    }

    public void setPageStyleChecked(PageStyle pageStyle){
        currentChecked = pageStyle.ordinal();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class PageHolder extends RecyclerView.ViewHolder{
        private View mReadBg;
        private ImageView mIvChecked;
        public PageHolder(@NonNull View itemView) {
            super(itemView);
            mReadBg = itemView.findViewById(R.id.read_bg_view);
            mIvChecked = itemView.findViewById(R.id.read_bg_iv_checked);
        }
    }
}
