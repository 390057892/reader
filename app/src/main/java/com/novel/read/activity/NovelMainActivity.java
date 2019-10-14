package com.novel.read.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.base.NovelBaseActivity;
import com.novel.read.constants.Constant;
import com.novel.read.event.HideBottomBarEvent;
import com.novel.read.event.LoginEvent;
import com.novel.read.event.SwitchFragmentEvent;
import com.novel.read.event.UpdateBookEvent;
import com.novel.read.event.VersionEvent;
import com.novel.read.fragment.BookFragment;
import com.novel.read.fragment.MoreFragment;
import com.novel.read.fragment.RecommendFragment;
import com.novel.read.fragment.StackFragment;
import com.novel.read.http.AccountManager;
import com.novel.read.model.db.dbManage.BookRepository;
import com.novel.read.utlis.DateUtli;
import com.novel.read.utlis.SpUtil;
import com.novel.read.utlis.ToastUtil;
import com.novel.read.utlis.VersionUtil;
import com.novel.read.widget.dialog.AppraiseDialog;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.BindView;

public class NovelMainActivity extends NovelBaseActivity {

    @BindView(R.id.fl_content)
    FrameLayout flContent;

    BottomNavigationView bottomBar;

    private List<Fragment> mFragmentList;
    private Fragment mCurrentFrag;
    private BookFragment mMainFragment;
    private RecommendFragment mRecommendFragment;
    private StackFragment mStackFragment;
    private MoreFragment mMoreFragment;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        bottomBar = findViewById(R.id.bottom_bar);
        mCurrentFrag = new Fragment();
        mMainFragment = BookFragment.newInstance();
        mRecommendFragment = RecommendFragment.newInstance();
        mStackFragment = StackFragment.newInstance();
        mMoreFragment = MoreFragment.newInstance();


        //计算apk的启动次数
        int count = SpUtil.getIntValue(Constant.InstallCount, 0);
        SpUtil.setIntValue(Constant.InstallCount, count + 1);
        Log.e("count", "count: " + count);
        AccountManager.getInstance().login(this);
        AccountManager.getInstance().checkVersion(VersionUtil.getPackageCode(this));
    }

    @Override
    protected void initData() {
        bottomBar.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.tab_one:
                    switchFragment(mMainFragment);
                    return true;
                case R.id.tab_two:
                    switchFragment(mRecommendFragment);
                    return true;
                case R.id.tab_three:
                    switchFragment(mStackFragment);
                    return true;
                case R.id.tab_four:
                    switchFragment(mMoreFragment);
                    return true;
            }
            return false;
        });

        if (BookRepository.getInstance().getCollBooks().size() > 0) {
            switchFragment(mMainFragment);
        } else {
            bottomBar.setSelectedItemId(R.id.tab_two);
        }
    }

    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (mCurrentFrag != null) {
                transaction.hide(mCurrentFrag);

            }
            transaction.add(R.id.fl_content, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction.hide(mCurrentFrag).show(targetFragment);

        }
        mCurrentFrag = targetFragment;
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventManager.Companion.getInstance().registerSubscriber(this);
        if (SpUtil.getLongValue(Constant.InstallTime) == 0) {
            SpUtil.setLongValue(Constant.InstallTime, System.currentTimeMillis());
        } else {
            if (DateUtli.checkInstallTime()&&!SpUtil.getBooleanValue(Constant.AppraiseShow)) {
                SpUtil.setBooleanValue(Constant.AppraiseShow, true);
                final AppraiseDialog dialog = new AppraiseDialog(this);
                dialog.AppraiseDialog(view -> {
                    goToMarket(this, VersionUtil.getPackage(this));
                    dialog.dismiss();
                });
                dialog.show();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
    }

    public static void goToMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        final String GOOGLE_PLAY = "com.android.vending";//这里对应的是谷歌商店，跳转别的商店改成对应的即可

        goToMarket.setPackage(GOOGLE_PLAY);//这里对应的是谷歌商店，跳转别的商店改成对应的即可

        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            if (goToMarket.resolveActivity(context.getPackageManager()) != null) { //有浏览器
                context.startActivity(goToMarket);
            }else {
                ToastUtil.show(context,"未检测到Google应用商店");
            }
            e.printStackTrace();
        }
    }

    @Subscribe
    public void checkVersion(VersionEvent event) {
        if (event.isFail()) {

        } else {
            if (TextUtils.isEmpty(event.getResult().getVersion().getSize())) {
                return;
            }
            //版本大小不为空 去更新。
        }
    }

    @Subscribe
    public void login(LoginEvent event) {
        if (event.isFail()) {
            Log.e("NovelMainActivity", "login: " + event.getEr().getMsg());
        } else {
            SpUtil.setStringValue(Constant.Uid, String.valueOf(event.getResult().getUid()));
        }
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!isVisible(bottomBar)) {
                bottomBar.setVisibility(View.VISIBLE);
                mMainFragment.updateBook(new UpdateBookEvent());
            } else {
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 1000) {
                    firstTime = secondTime;
                    ToastUtil.show(NovelMainActivity.this, "再次点击退出界面");
                } else {
                    finish();
                }
            }
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Subscribe
    public void setBottomBar(HideBottomBarEvent event) {
        if (event.getResult()) {
            bottomBar.setVisibility(View.GONE);
        } else {
            bottomBar.setVisibility(View.VISIBLE);
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void reStart(Context context) {
        Intent intent = new Intent(context, NovelMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Subscribe
    public void toRecommendFragment(SwitchFragmentEvent event) {
//        switchFragment(mRecommendFragment);
        bottomBar.setSelectedItemId(R.id.tab_two);
    }
}
