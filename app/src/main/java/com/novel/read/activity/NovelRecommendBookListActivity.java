package com.novel.read.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.novel.read.R;
import com.novel.read.adapter.ViewPageAdapter;
import com.novel.read.base.NovelBaseActivity;
import com.novel.read.constants.Constant;
import com.novel.read.fragment.BookListFragment;
import com.novel.read.widget.VpTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NovelRecommendBookListActivity extends NovelBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vp_tab)
    VpTabLayout mVpTab;
    @BindView(R.id.vp_recommend_type)
    ViewPager mVpRecommendType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recommend_book_list;
    }

    @Override
    protected void initView() {
        List<Fragment> fragmentList = new ArrayList<>();
        String sex = getIntent().getStringExtra(Constant.Sex);
        String type = getIntent().getStringExtra(Constant.Type);
        if (type.equals(Constant.ListType.Human)){
            toolbar.setTitle(getString(R.string.popular_selection));
        }else if (type.equals(Constant.ListType.EditRecommend)){
            toolbar.setTitle(getString(R.string.edit_recommend));
        }else if (type.equals(Constant.ListType.HotSearch)){
            toolbar.setTitle(getString(R.string.hot_search));
        }

        BookListFragment generalFragment = BookListFragment.newInstance(type, Constant.DateTyp.General, sex);
        BookListFragment monthFragment = BookListFragment.newInstance(type, Constant.DateTyp.Month, sex);
        BookListFragment weekFragment = BookListFragment.newInstance(type, Constant.DateTyp.Week, sex);
        fragmentList.add(generalFragment);
        fragmentList.add(monthFragment);
        fragmentList.add(weekFragment);
        ViewPageAdapter pageAdapter = new ViewPageAdapter(getSupportFragmentManager(), fragmentList);
        mVpRecommendType.setAdapter(pageAdapter);
        mVpRecommendType.setOffscreenPageLimit(2);
        mVpRecommendType.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mVpTab.setAnim(position, mVpRecommendType);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void initData() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mVpTab.setOnTabBtnClickListener(new VpTabLayout.OnTabClickListener() {
            @Override
            public void onTabBtnClick(VpTabLayout.CommonTabBtn var1, View var2) {
                switch (var1) {
                    case ONE:
                        mVpTab.setAnim(0, mVpRecommendType);
                        break;
                    case TWO:
                        mVpTab.setAnim(1, mVpRecommendType);
                        break;
                    case THREE:
                        mVpTab.setAnim(2, mVpRecommendType);
                        break;
                }
            }
        });
    }
}
