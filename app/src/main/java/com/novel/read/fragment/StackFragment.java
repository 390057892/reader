package com.novel.read.fragment;

import android.os.Bundle;

import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.activity.NovelSearchActivity;
import com.novel.read.adapter.StackAdapter;
import com.novel.read.base.NovelBaseFragment;
import com.novel.read.event.GetCategoryTypeEvent;
import com.novel.read.http.AccountManager;
import com.novel.read.model.protocol.CategoryTypeResp;
import com.novel.read.widget.RefreshLayout;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * create by 赵利君 on 2019/6/10
 * describe:
 */
public class StackFragment extends NovelBaseFragment {

    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.rlv_book_type)
    RecyclerView mRlBookType;
    private StackAdapter mAdapter;
    @BindView(R.id.refresh)
    RefreshLayout refreshLayout;
    private List<CategoryTypeResp.CategoryBean> mList;

    public static StackFragment newInstance() {
        Bundle args = new Bundle();
        StackFragment fragment = new StackFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_stack;
    }

    @Override
    protected void initView() {
        EventManager.Companion.getInstance().registerSubscriber(this);
        mList = new ArrayList<>();
        mRlBookType.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mAdapter = new StackAdapter(mList);
        mRlBookType.setAdapter(mAdapter);

    }

    @Override
    protected void initData() {
        refreshLayout.showLoading();
        getData();
        refreshLayout.setOnReloadingListener(new RefreshLayout.OnReloadingListener() {
            @Override
            public void onReload() {
                getData();
            }
        });
    }

    private void getData(){
        AccountManager.getInstance().getCategoryType();
    }



    @OnClick(R.id.tv_search)
    public void onViewClicked() {
        toActivity(NovelSearchActivity.class);
        getActivity().overridePendingTransition(R.anim.message_fade_in, R.anim.message_fade_out);
    }

    @Subscribe
    public void getCategoryType(GetCategoryTypeEvent event){
        refreshLayout.showFinish();
        if (event.isFail()){
            refreshLayout.showError();
        }else {
            mList.clear();
            mList.addAll(event.getResult().getCategory());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
    }
}
