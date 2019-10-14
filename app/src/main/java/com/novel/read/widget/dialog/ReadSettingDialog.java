package com.novel.read.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.read.R;
import com.novel.read.adapter.PageStyleAdapter;
import com.novel.read.widget.page.PageLoader;
import com.novel.read.widget.page.PageMode;
import com.novel.read.widget.page.PageStyle;
import com.novel.read.widget.page.ReadSettingManager;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReadSettingDialog extends Dialog {

    private static final String TAG = "ReadSettingDialog";

    @BindView(R.id.tv_simple)
    TextView mTvSimple;
    @BindView(R.id.tv_trans)
    TextView mTvTrans;

    @BindView(R.id.read_setting_tv_font_minus)
    ImageView mTvFontMinus;

    @BindView(R.id.read_setting_tv_font_plus)
    ImageView mTvFontPlus;

    @BindView(R.id.read_setting_rv_bg)
    RecyclerView mRvBg;

    /************************************/
    private PageStyleAdapter mPageStyleAdapter;
    private ReadSettingManager mSettingManager;
    private PageLoader mPageLoader;
    private Activity mActivity;

    private PageMode mPageMode;
    private PageStyle mPageStyle;

    private int mBrightness;
    private int mTextSize;

    private boolean isBrightnessAuto;
    private boolean isTextDefault;

    private int convertType;

    public ReadSettingDialog(@NonNull Activity activity, PageLoader mPageLoader) {
        super(activity, R.style.ReadSettingDialog);
        mActivity = activity;
        this.mPageLoader = mPageLoader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);
        ButterKnife.bind(this);
        setUpWindow();
        initData();
        initWidget();
        initClick();
    }

    //设置Dialog显示的位置
    private void setUpWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    private void initData() {
        mSettingManager = ReadSettingManager.getInstance();

        isBrightnessAuto = mSettingManager.isBrightnessAuto();
        mBrightness = mSettingManager.getBrightness();
        mTextSize = mSettingManager.getTextSize();
        isTextDefault = mSettingManager.isDefaultTextSize();
        mPageMode = mSettingManager.getPageMode();
        mPageStyle = mSettingManager.getPageStyle();
        convertType = mSettingManager.getConvertType();
        if (convertType == 0) {
            mTvSimple.setSelected(true);
            mTvTrans.setSelected(false);
        } else {
            mTvSimple.setSelected(false);
            mTvTrans.setSelected(true);
        }
    }

    private void initWidget() {
        //RecyclerView
        setUpAdapter();
    }

    private void setUpAdapter() {
        Drawable[] drawables = {
                getDrawable(R.color.nb_read_bg_1)
                , getDrawable(R.color.nb_read_bg_2)
                , getDrawable(R.color.nb_read_bg_4)
                , getDrawable(R.color.nb_read_bg_5)};

        mPageStyleAdapter = new PageStyleAdapter(Arrays.asList(drawables), mPageLoader);
        mRvBg.setLayoutManager(new GridLayoutManager(getContext(), 5));
        mRvBg.setAdapter(mPageStyleAdapter);

        mPageStyleAdapter.setPageStyleChecked(mPageStyle);

    }


    private Drawable getDrawable(int drawRes) {
        return ContextCompat.getDrawable(getContext(), drawRes);
    }

    private void initClick() {

        //字体大小调节
        mTvFontMinus.setOnClickListener(
                (v) -> {
                    int fontSize = mSettingManager.getTextSize() - 1;
                    if (fontSize < 0) return;
                    mPageLoader.setTextSize(fontSize);
                }
        );

        mTvFontPlus.setOnClickListener(
                (v) -> {
                    int fontSize = mSettingManager.getTextSize() + 1;
                    mPageLoader.setTextSize(fontSize);
                }
        );

        mTvSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (convertType == 0) {
                    return;
                }
                mTvSimple.setSelected(true);
                mTvTrans.setSelected(false);
                mSettingManager.setConvertType(0);
                convertType = 0;
                mPageLoader.setTextSize(mSettingManager.getTextSize());
            }
        });

        mTvTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (convertType == 1) {
                    return;
                }
                mTvSimple.setSelected(false);
                mTvTrans.setSelected(true);
                mSettingManager.setConvertType(1);
                convertType = 1;
                mPageLoader.setTextSize(mSettingManager.getTextSize());
            }
        });

    }

}
