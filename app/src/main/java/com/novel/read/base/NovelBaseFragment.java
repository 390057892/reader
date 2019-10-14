package com.novel.read.base;


import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class NovelBaseFragment extends Fragment {

    private Unbinder bind;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mContextView = inflater.inflate(getLayoutId(), container, false);
        bind = ButterKnife.bind(this, mContextView);
        initView();
        initData();
        return mContextView;
    }


    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    public void toActivity(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }

    public void toActivity(Class<?> toClsActivity, Bundle bundle) {
        Intent intent = new Intent(getActivity(), toClsActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }
}
