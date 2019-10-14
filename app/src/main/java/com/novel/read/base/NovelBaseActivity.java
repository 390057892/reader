package com.novel.read.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import com.novel.read.R;
import com.novel.read.constants.Constant;
import com.novel.read.utlis.LocalManageUtil;
import com.novel.read.utlis.SpUtil;
import com.novel.read.utlis.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * create by 赵利君 on 2019/6/10
 * describe:
 */
public abstract class NovelBaseActivity extends AppCompatActivity {
    Unbinder mBind;
    private boolean mCheckNet = true;//是否检查网络连接
    public boolean mNetworkChange = false;//获取网络是否连接
    private boolean mNowMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setBarsStyle(this, R.color.colorPrimary, true);
        mNowMode = SpUtil.getBooleanValue(Constant.NIGHT);
        setContentView(getLayoutId());
        mBind = ButterKnife.bind(this);
        initView();
        initData();

    }

    protected void setTheme() {
        if (SpUtil.getBooleanValue(Constant.NIGHT) != mNowMode) {
            if (SpUtil.getBooleanValue(Constant.NIGHT)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }

    public void toActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void toActivity(Class<?> toClsActivity, Bundle bundle) {
        Intent intent = new Intent(this, toClsActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    public boolean isNetworkChange() {
        return mNetworkChange;
    }

    public void setNetworkChange(boolean mNetworkChange) {
        this.mNetworkChange = mNetworkChange;
    }

    public boolean getIsCheckNet() {
        return mCheckNet;
    }

    public void setIsCheckNet(boolean checkNet) {
        this.mCheckNet = checkNet;
    }


    @Override
    protected void onResume() {
        super.onResume();
        setTheme();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }
}
