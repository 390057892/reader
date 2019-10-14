package com.novel.read.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.read.R;
import com.novel.read.model.protocol.MarkResp;

import java.util.List;

public class MarkAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private List<MarkResp.SignBean> mList;
    private boolean edit;

    public MarkAdapter(List<MarkResp.SignBean> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext==null){
            mContext = viewGroup.getContext();
        }
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_mark, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolder){
            if (edit){
                ((ViewHolder) viewHolder).mCheck.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mList.get(i).setEdit(b);
                    }
                });
            }else {
                ((ViewHolder) viewHolder).mCheck.setVisibility(View.GONE);
            }
            ((ViewHolder) viewHolder).mTvMark.setText(mList.get(i).getContent());
            ((ViewHolder) viewHolder).mCheck.setChecked(mList.get(i).isEdit());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTvMark;
        CheckBox mCheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvMark = itemView.findViewById(R.id.tvMarkItem);
            mCheck = itemView.findViewById(R.id.checkbox);
        }
    }

    public void setEdit(boolean edit){
        this.edit = edit;
        notifyDataSetChanged();
    }

    public boolean getEdit(){
        return edit;
    }


    public String getSelectList() {
        StringBuilder signs = new StringBuilder();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isEdit()) {
                if (signs.equals("")){
                    signs.append(mList.get(i).getId());
                }else {
                    signs.append(",").append(mList.get(i).getId());
                }
            }
        }
        return String.valueOf(signs);
    }
}
