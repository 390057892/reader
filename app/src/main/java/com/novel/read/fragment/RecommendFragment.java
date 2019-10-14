package com.novel.read.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.activity.NovelSearchActivity;
import com.novel.read.base.NovelBaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * create by 赵利君 on 2019/6/10
 * describe:
 */
public class RecommendFragment extends NovelBaseFragment {

    @BindView(R.id.tv_search)
    TextView mTvSearch;

    public static RecommendFragment newInstance() {
        Bundle args = new Bundle();
        RecommendFragment fragment = new RecommendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend;
    }

    @Override
    protected void initView() {
        EventManager.Companion.getInstance().registerSubscriber(this);
//        List<Fragment> fragmentList = new ArrayList<>();
//        ManFragment manFragment = ManFragment.newInstance(Constant.GenderType.Man);
//        WomanFragment womanFragment = WomanFragment.newInstance(Constant.GenderType.Woman);
//        fragmentList.add(manFragment);
//        fragmentList.add(womanFragment);
    }

    @Override
    protected void initData() {
    }

    @OnClick({ R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                toActivity(NovelSearchActivity.class);
                getActivity().overridePendingTransition(R.anim.message_fade_in, R.anim.message_fade_out);
                break;
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
    }
}
