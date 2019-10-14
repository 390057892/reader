package com.novel.read.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.activity.NovelRankListActivity;
import com.novel.read.activity.NovelRecommendBookListActivity;
import com.novel.read.adapter.EditRecommendAdapter;
import com.novel.read.adapter.HumanAdapter;
import com.novel.read.adapter.RankAdapter;
import com.novel.read.base.NovelBaseFragment;
import com.novel.read.constants.Constant;
import com.novel.read.http.AccountManager;
import com.novel.read.model.protocol.RecommendListResp;
import com.novel.read.widget.HeadLayout;
import com.novel.read.widget.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WomanFragment extends NovelBaseFragment {


    @BindView(R.id.head_pop)
    HeadLayout mHeadPop;
    @BindView(R.id.rlv_pop)
    RecyclerView mRlvPop;
    @BindView(R.id.head_recommend)
    HeadLayout headRecommend;
    @BindView(R.id.rlv_recommend)
    RecyclerView mRlvRecommend;
    @BindView(R.id.head_update)
    HeadLayout headUpdate;
    @BindView(R.id.rlv_update)
    RecyclerView mRlvUpdate;
    @BindView(R.id.swipe)
    RefreshLayout refreshLayout;

    private HumanAdapter mHumanAdapter;
    private List<RecommendListResp.ListBean> mHumanList = new ArrayList<>();
    private EditRecommendAdapter mEditAdapter;
    private List<RecommendListResp.ListBean> mEditList = new ArrayList<>();
    private RankAdapter mRankAdapter;
    private List<RecommendListResp.ListBean> mRankList = new ArrayList<>();

    public static WomanFragment newInstance(String sex) {
        Bundle args = new Bundle();
        args.putString(Constant.Sex, sex);
        WomanFragment fragment = new WomanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_man;
    }

    @Override
    protected void initView() {
        EventManager.Companion.getInstance().registerSubscriber(this);

        mRlvPop.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mHumanAdapter = new HumanAdapter(mHumanList);
        mRlvPop.setAdapter(mHumanAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRlvRecommend.setLayoutManager(linearLayoutManager);
        mEditAdapter = new EditRecommendAdapter(mEditList);
        mRlvRecommend.setAdapter(mEditAdapter);

        mRlvUpdate.setLayoutManager(new GridLayoutManager(getActivity(),3));
        mRankAdapter = new RankAdapter(mRankList);
        mRlvUpdate.setAdapter(mRankAdapter);


    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            String sex = getArguments().getString(Constant.Sex);
            refreshLayout.showLoading();
            getData(sex);
            refreshLayout.setOnReloadingListener(new RefreshLayout.OnReloadingListener() {
                @Override
                public void onReload() {
                    getData(sex);
                }
            });

        }
        mHeadPop.setRightTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void getData(String sex){
        AccountManager.getInstance().getRecommendList(Constant.ListType.Human,new HumanCallBack());
        AccountManager.getInstance().getRecommendList(Constant.ListType.EditRecommend,new EditCallBack());
        AccountManager.getInstance().getRecommendList(Constant.ListType.HotSearch,new HotSearchCallBack());
//        AccountManager.getInstance().getRankByUpdate(1,9,new HotSearchCallBack());
    }


    @OnClick({R.id.head_pop, R.id.head_recommend, R.id.head_update})
    public void onViewClicked(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.Sex,Constant.GenderType.Woman);
        switch (view.getId()) {
            case R.id.head_pop:
                bundle.putString(Constant.Type,Constant.ListType.Human);
                toActivity(NovelRecommendBookListActivity.class,bundle);
                break;
            case R.id.head_recommend:
                bundle.putString(Constant.Type,Constant.ListType.EditRecommend);
                toActivity(NovelRankListActivity.class,bundle);
                break;
            case R.id.head_update:
                bundle.putString(Constant.Type,Constant.ListType.HotSearch);
                toActivity(NovelRankListActivity.class,bundle);
                break;
        }
    }

    private class HumanCallBack implements Callback<RecommendListResp> {

        @Override
        public void onResponse(@NonNull Call<RecommendListResp> call, @NonNull Response<RecommendListResp> response) {
            if (response.isSuccessful()&& response.body() != null){
                mHumanList.clear();
                mHumanList.addAll(response.body().getList());
                mHumanAdapter.notifyDataSetChanged();

            }else {
                refreshLayout.showError();
            }
        }

        @Override
        public void onFailure(Call<RecommendListResp> call, Throwable t) {

        }
    }

    private class EditCallBack implements Callback<RecommendListResp> {

        @Override
        public void onResponse(@NonNull Call<RecommendListResp> call, @NonNull Response<RecommendListResp> response) {
            if (response.isSuccessful()&& response.body() != null){
                mEditList.clear();
                mEditList.addAll(response.body().getList());
                mEditAdapter.notifyDataSetChanged();

            }else {
                refreshLayout.showError();
            }
        }

        @Override
        public void onFailure(Call<RecommendListResp> call, Throwable t) {

        }
    }

    private class HotSearchCallBack implements Callback<RecommendListResp> {

        @Override
        public void onResponse(@NonNull Call<RecommendListResp> call, @NonNull Response<RecommendListResp> response) {
            refreshLayout.showFinish();
            if (response.isSuccessful()&&response.body()!=null){
                mRankList.clear();
                mRankList.addAll(response.body().getList());
                mRankAdapter.notifyDataSetChanged();
            }else {
                refreshLayout.showError();
            }
        }

        @Override
        public void onFailure(Call<RecommendListResp> call, Throwable t) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
    }
}
