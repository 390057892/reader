package com.novel.read.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.novel.read.R;

public class AppraiseDialog extends Dialog {
    private View view;
    private TextView mTvSure;
    public AppraiseDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_go_appraise, null);
        view.setHasTransientState(true);
        setContentView(view,new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        setCancelable(false);
        initView();

    }

    private void initView() {
        TextView mTvCancel = findViewById(R.id.tv_cancel);
        mTvSure = findViewById(R.id.tv_appraise);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void AppraiseDialog(View.OnClickListener receiveClickListener){
        mTvSure.setOnClickListener(receiveClickListener);
    }

}
