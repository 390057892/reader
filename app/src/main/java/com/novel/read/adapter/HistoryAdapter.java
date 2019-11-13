package com.novel.read.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.read.R;
import com.novel.read.model.db.SearchListTable;

import java.util.List;

/**
 * create by zlj on 2019/6/17
 * describe:
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<SearchListTable> mList;
    private Context mContext;
    protected OnItemClickListener mClickListener;

    public HistoryAdapter(List<SearchListTable> mList) {
        this.mList = mList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView mTvLabel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvLabel = itemView.findViewById(R.id.tv_label);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_label, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mTvLabel.setText(mList.get(i).getKey());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view,i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mClickListener = mListener;
    }


    public interface OnItemClickListener{
        void onItemClick(View view, int pos);
    }
}
