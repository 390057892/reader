package com.novel.read.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.read.R;
import com.novel.read.constants.Constant;

import java.util.List;

/**
 * create by 赵利君 on 2019/6/17
 * describe:
 */
public class HotAdapter extends RecyclerView.Adapter<HotAdapter.ViewHolder> {

    private List<String> mList;
    private Context mContext;
    protected OnItemClickListener mClickListener;

    public HotAdapter(List<String> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext==null){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_label, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.mTvLabel.setText(mList.get(i));
        viewHolder.mTvLabel.setBackgroundColor(Constant.tagColors[i]);
        viewHolder.itemView.setOnClickListener(view -> mClickListener.onItemClick(view,i));

    }

    @Override
    public int getItemCount() {
        if (mList.size()>8){
            return 8;
        }
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTvLabel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvLabel = itemView.findViewById(R.id.tv_label);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mClickListener = mListener;
    }


    public interface OnItemClickListener{
        void onItemClick(View view, int pos);
    }
}
