package com.novel.read.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.novel.read.R;

public class VpTabLayout extends ConstraintLayout implements View.OnClickListener {

    private boolean animFinish = true; //防止动画还未结束就开始另一个动画
    private TextView mTvOne;
    private TextView mTvTwo;
    private TextView mTvThree;
    private View mView;
    private VpTabLayout.OnTabClickListener mBtnClickListener;
    private Context mContext;

    public VpTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widget_tab, this, true);
        initViews();
        initAttr(attrs);
    }

    private void initViews() {
        mTvOne = findViewById(R.id.tv_one);
        mTvTwo = findViewById(R.id.tv_second);
        mTvThree = findViewById(R.id.tv_third);
        mView = findViewById(R.id.view);
        mTvOne.setOnClickListener(this);
        mTvTwo.setOnClickListener(this);
        mTvThree.setOnClickListener(this);
    }


    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.VpTabLayout);
        int oneText = typedArray.getResourceId(R.styleable.VpTabLayout_oneText, R.string.empty_info);
        int twoText = typedArray.getResourceId(R.styleable.VpTabLayout_twoText, R.string.empty_info);
        int threeText = typedArray.getResourceId(R.styleable.VpTabLayout_threeText, R.string.empty_info);

        mTvOne.setText(oneText);
        mTvTwo.setText(twoText);
        mTvThree.setText(threeText);

        typedArray.recycle();
    }


    //设置动画
    public void setAnim(final int position, final ViewPager viewPager) {
        if (!animFinish) {
            return;
        }
        animFinish = false;
        ValueAnimator animator = null;
        switch (position) {
            case 0:
                animator = ValueAnimator.ofFloat(mView.getX(), mTvOne.getX());
                animator.setTarget(mTvOne);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mView.setX((Float) valueAnimator.getAnimatedValue());
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        viewPager.setCurrentItem(position);
                        mTvOne.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                        mTvTwo.setTextColor(ContextCompat.getColor(mContext,R.color.colorTitle));
                        mTvThree.setTextColor(ContextCompat.getColor(mContext,R.color.colorTitle));
                        animFinish = true;
                    }
                });
                break;
            case 1:
                animator = ValueAnimator.ofFloat(mView.getX(), mTvTwo.getX());
                animator.setTarget(mTvTwo);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mView.setX((Float) valueAnimator.getAnimatedValue());
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        viewPager.setCurrentItem(position);
                        mTvOne.setTextColor(ContextCompat.getColor(mContext,R.color.colorTitle));
                        mTvTwo.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                        mTvThree.setTextColor(ContextCompat.getColor(mContext,R.color.colorTitle));
                        animFinish = true;
                    }
                });
                break;
            case 2:
                animator = ValueAnimator.ofFloat(mView.getX(), mTvThree.getX());
                animator.setTarget(mTvThree);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mView.setX((Float) valueAnimator.getAnimatedValue());
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        viewPager.setCurrentItem(position);
                        mTvOne.setTextColor(ContextCompat.getColor(mContext,R.color.colorTitle));
                        mTvTwo.setTextColor(ContextCompat.getColor(mContext,R.color.colorTitle));
                        mTvThree.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                        animFinish = true;
                    }
                });
                break;
            default:
                break;

        }
        if (animator != null) {
            animator.setDuration(200);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.start();
        }
    }

    public void setOnTabBtnClickListener(@Nullable VpTabLayout.OnTabClickListener listener) {
        this.mBtnClickListener = listener;
    }

    public void onClick(View v) {
        if (this.mBtnClickListener != null) {
            if (v.equals(this.mTvOne)) {
                this.mBtnClickListener.onTabBtnClick(VpTabLayout.CommonTabBtn.ONE, v);
            } else if (v.equals(this.mTvTwo)) {
                this.mBtnClickListener.onTabBtnClick(VpTabLayout.CommonTabBtn.TWO, v);
            } else if (v.equals(this.mTvThree)) {
                this.mBtnClickListener.onTabBtnClick(VpTabLayout.CommonTabBtn.THREE, v);
            }
        }

    }

    public static enum CommonTabBtn {
        ONE,
        TWO,
        THREE;

        private CommonTabBtn() {
        }
    }

    public interface OnTabClickListener {
        void onTabBtnClick(VpTabLayout.CommonTabBtn var1, View var2);
    }
}
