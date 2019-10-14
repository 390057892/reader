package com.novel.read.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.base.NovelBaseActivity;
import com.novel.read.constants.Constant;
import com.novel.read.event.UpdateBookEvent;
import com.novel.read.event.VersionEvent;
import com.novel.read.http.AccountManager;
import com.novel.read.model.protocol.VersionResp;
import com.novel.read.utlis.CleanCacheUtils;
import com.novel.read.utlis.LocalManageUtil;
import com.novel.read.utlis.SpUtil;
import com.novel.read.utlis.VersionUtil;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

public class NovelSettingActivity extends NovelBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_language)
    TextView mTvLanguage;
    @BindView(R.id.tv_cache_num)
    TextView mTvCacheNum;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.tv_check)
    TextView mTvCheck;
    private VersionResp resp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        EventManager.Companion.getInstance().registerSubscriber(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        mTvLanguage.setText(getResources().getStringArray(R.array.setting_dialog_language_choice)[SpUtil.getIntValue(Constant.Language, 1)]);
        mTvVersion.setText("V" + VersionUtil.getPackageName(this));
        try {
            final String cacheSize = CleanCacheUtils.getInstance().getTotalCacheSize(NovelSettingActivity.this);
            mTvCacheNum.setText(cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar.setNavigationOnClickListener(view -> finish());

        AccountManager.getInstance().checkVersion(VersionUtil.getPackageCode(this));
    }

    @OnClick({R.id.ll_choose_language, R.id.ll_clear_cache, R.id.tv_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_choose_language:
                showLanguageDialog();
                break;
            case R.id.ll_clear_cache:
                //默认不勾选清空书架列表，防手抖！！
                final boolean[] selected = {true, false};
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.clear_cache))
                        .setCancelable(true)
                        .setMultiChoiceItems(new String[]{getString(R.string.clear_cache), getString(R.string.clear_book)}, selected, (dialog, which, isChecked) -> selected[which] = isChecked)
                        .setPositiveButton(getString(R.string.sure), (dialog, which) -> {
                            new Thread(() -> {
                                CleanCacheUtils.getInstance().clearCache(selected[0], selected[1], NovelSettingActivity.this);
                                String cacheSize = "";
                                try {
                                    cacheSize = CleanCacheUtils.getInstance().getTotalCacheSize(NovelSettingActivity.this);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String finalCacheSize = cacheSize;
                                runOnUiThread(() -> {
                                    EventManager.Companion.getInstance().postEvent(new UpdateBookEvent());
                                    mTvCacheNum.setText(finalCacheSize);
                                });
                            }).start();
                            dialog.dismiss();
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                        .create().show();
                break;
            case R.id.tv_check:
                //版本大小不为空 去更新。
                updateApk(resp);
                break;
        }
    }


    public void showLanguageDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose_language))
                .setSingleChoiceItems(getResources().getStringArray(R.array.setting_dialog_language_choice),
                        SpUtil.getIntValue(Constant.Language, 1),
                        (dialog, which) -> {
                            String language = getResources().getStringArray(R.array.setting_dialog_language_choice)[which];
                            mTvLanguage.setText(language);
                            SpUtil.setIntValue(Constant.Language, which);
                            dialog.dismiss();

                            if (which == 0) {
                                selectLanguage(0);
                            } else {
                                selectLanguage(1);
                            }
                        })
                .create().show();
    }

    private void selectLanguage(int select) {
        LocalManageUtil.saveSelectLanguage(this, select);
        NovelMainActivity.reStart(this);
    }

    @Subscribe
    public void checkVersion(VersionEvent event){
        if (event.isFail()){

        }else {
            if (TextUtils.isEmpty(event.getResult().getVersion().getSize())){
                return;
            }
            resp = event.getResult();
            mTvCheck.setVisibility(View.VISIBLE);

        }
    }

    private void updateApk(VersionResp resp){
        VersionResp.VersionBean versionBean = resp.getVersion();
        DownloadBuilder builder = AllenVersionChecker
                .getInstance()
                .downloadOnly(UIData.create()
                        .setTitle(getString(R.string.new_version,versionBean.getVersion()))
                        .setContent(versionBean.getContent())
                        .setDownloadUrl(versionBean.getDownload())
                );
        builder.executeMission(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
    }
}
