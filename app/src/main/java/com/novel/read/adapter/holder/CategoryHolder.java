package com.novel.read.adapter.holder;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.novel.read.R;
import com.novel.read.adapter.ViewHolderImpl;
import com.novel.read.model.db.dbManage.BookManager;
import com.novel.read.utlis.StringUtils;
import com.novel.read.widget.page.TxtChapter;

public class CategoryHolder extends ViewHolderImpl<TxtChapter> {

    private TextView mTvChapter;

    @Override
    public void initView() {
        mTvChapter = findById(R.id.category_tv_chapter);
    }

    @Override
    public void onBind(TxtChapter value, int pos){
        //首先判断是否该章已下载
        Drawable drawable = null;
        if (value.getBookId() != null && BookManager.isChapterCached(value.getBookId(),value.getTitle())){
            drawable = ContextCompat.getDrawable(getContext(),R.drawable.selector_category_load);
        }
        else {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_unload);
        }

        mTvChapter.setSelected(false);
        mTvChapter.setTextColor(ContextCompat.getColor(getContext(),R.color.colorTitle));
        mTvChapter.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
        mTvChapter.setText(StringUtils.convertCC(value.getTitle()));

    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.rlv_item_category;
    }

    public void setSelectedChapter(){
        mTvChapter.setTextColor(ContextCompat.getColor(getContext(),R.color.light_red));
        mTvChapter.setSelected(true);
    }
}
