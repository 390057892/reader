package com.novel.read.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.novel.read.adapter.holder.CategoryHolder;
import com.novel.read.widget.page.TxtChapter;

public class CategoryAdapter extends EasyAdapter<TxtChapter> {
    private int currentSelected = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        CategoryHolder holder = (CategoryHolder) view.getTag();

        if (position == currentSelected){
            holder.setSelectedChapter();
        }

        return view;
    }

    @Override
    protected IViewHolder<TxtChapter> onCreateViewHolder(int viewType) {
        return new CategoryHolder();
    }

    public void setChapter(int pos){
        currentSelected = pos;
        notifyDataSetChanged();
    }

}
