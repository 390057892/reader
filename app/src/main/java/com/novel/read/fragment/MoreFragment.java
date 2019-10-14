package com.novel.read.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.activity.NovelSearchActivity;
import com.novel.read.activity.NovelSettingActivity;
import com.novel.read.base.NovelBaseFragment;
import com.novel.read.constants.Constant;
import com.novel.read.event.ReStartEvent;
import com.novel.read.utlis.ToastUtil;
import com.novel.read.utlis.VersionUtil;
import com.novel.read.widget.dialog.AppraiseDialog;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * create by 赵利君 on 2019/6/10
 * describe:
 */
public class MoreFragment extends NovelBaseFragment {

    @BindView(R.id.toolbar)
    Toolbar title;
    @BindView(R.id.tv_options)
    TextView tvOptions;
    @BindView(R.id.tv_appraise)
    TextView tvAppraise;
    @BindView(R.id.tv_setting)
    TextView tvSetting;


    public static MoreFragment newInstance() {
        Bundle args = new Bundle();
        MoreFragment fragment = new MoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void initView() {
        EventManager.Companion.getInstance().registerSubscriber(this);
        title.inflateMenu(R.menu.title_more);
    }

    @Override
    protected void initData() {
        title.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.action_search) {
                toActivity(NovelSearchActivity.class);
                getActivity().overridePendingTransition(R.anim.message_fade_in, R.anim.message_fade_out);
            }
            return true;
        });
    }


    @OnClick({R.id.tv_options, R.id.tv_appraise, R.id.tv_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_options:
                feedback();
                break;
            case R.id.tv_appraise:
                final AppraiseDialog dialog = new AppraiseDialog(getActivity());
                dialog.AppraiseDialog(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToMarket(getActivity(), VersionUtil.getPackage(getActivity()));
                        dialog.dismiss();
                    }
                });
                dialog.show();

                break;
            case R.id.tv_setting:
                toActivity(NovelSettingActivity.class);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

    public void feedback() {
        Intent email = new Intent(Intent.ACTION_SEND);
        //邮件发送类型：无附件，纯文本
        email.setType("plain/text");
        //邮件接收者（数组，可以是多位接收者）
        String[] emailReceiver = new String[]{Constant.FeedBackEmail};
        String emailTitle = getString(R.string.opinions);
        String emailContent = "";
        //设置邮件地址
        email.putExtra(Intent.EXTRA_EMAIL, emailReceiver);
        //设置邮件标题
        email.putExtra(Intent.EXTRA_SUBJECT, emailTitle);
        //设置发送的内容
        email.putExtra(Intent.EXTRA_TEXT, emailContent);
        //调用系统的邮件系统
        startActivity(Intent.createChooser(email, "请选择邮件发送软件"));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
    }

    @Subscribe
    public void restart(ReStartEvent event) {
        getActivity().recreate();
    }
}
