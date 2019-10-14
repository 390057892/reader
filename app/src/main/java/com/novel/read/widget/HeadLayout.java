package com.novel.read.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.novel.read.R;

public class HeadLayout extends LinearLayout {

    private TextView mTvHead;
    private TextView mTvMore;
    public HeadLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widget_head, this, true);
        initViews();
        initAttr(attrs);
    }


    private void initViews() {
        mTvHead=findViewById(R.id.tv_head);
        mTvMore = findViewById(R.id.tv_more);
    }
    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HeadLayout);
        boolean showHead = typedArray.getBoolean(R.styleable.HeadLayout_showHead, true);
        int headText=typedArray.getResourceId(R.styleable.HeadLayout_headText,R.string.empty_info);
        int headImg= typedArray.getResourceId(R.styleable.HeadLayout_headImg, R.mipmap.ic_launcher);
        boolean showRight = typedArray.getBoolean(R.styleable.HeadLayout_showRightText, true);
        int rightText=typedArray.getResourceId(R.styleable.HeadLayout_rightText,R.string.empty_info);
        int rightImg=typedArray.getResourceId(R.styleable.HeadLayout_rightImg,R.mipmap.ic_launcher);
        if (showHead){
            mTvHead.setText(headText);
            Drawable drawable = ContextCompat.getDrawable(getContext(), headImg);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mTvHead.setCompoundDrawables(drawable, null, null, null);
        }else {
            mTvHead.setVisibility(GONE);
        }
        if (showRight){
            mTvMore.setText(rightText);
            if (rightImg!=R.mipmap.ic_launcher){
                Drawable drawable = ContextCompat.getDrawable(getContext(), rightImg);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mTvHead.setCompoundDrawables(null, null, drawable, null);
            }
        }else {
            mTvMore.setVisibility(GONE);
        }

        typedArray.recycle();
    }

    //设置点击右边文字的监听
    public void setRightTextClickListener(OnClickListener listener) {
        mTvMore.setOnClickListener(listener);
    }


}
