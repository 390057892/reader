package com.novel.read.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager

import com.novel.read.R
import kotlinx.android.synthetic.main.widget_tab.view.*

class VpTabLayout(private val mContext: Context, attrs: AttributeSet) :
    ConstraintLayout(mContext, attrs), View.OnClickListener {

    private var animFinish = true //防止动画还未结束就开始另一个动画
    private var mBtnClickListener: OnTabClickListener? = null

    init {
        LayoutInflater.from(mContext).inflate(R.layout.widget_tab, this, true)
        initViews()
        initAttr(attrs)
    }

    private fun initViews() {
        tv_one.setOnClickListener(this)
        tv_second.setOnClickListener(this)
        tv_third.setOnClickListener(this)
    }


    private fun initAttr(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.VpTabLayout)
        val oneText = typedArray.getResourceId(R.styleable.VpTabLayout_oneText, R.string.empty_info)
        val twoText = typedArray.getResourceId(R.styleable.VpTabLayout_twoText, R.string.empty_info)
        val threeText =
            typedArray.getResourceId(R.styleable.VpTabLayout_threeText, R.string.empty_info)

        tv_one.setText(oneText)
        tv_second.setText(twoText)
        tv_third.setText(threeText)

        typedArray.recycle()
    }


    //设置动画
    fun setAnim(position: Int, viewPager: ViewPager) {
        if (!animFinish) {
            return
        }
        animFinish = false
        var animator: ValueAnimator? = null
        when (position) {
            0 -> {
                animator = ValueAnimator.ofFloat(view.x, tv_one.x)
                animator!!.setTarget(tv_one)
                animator.addUpdateListener { valueAnimator ->
                    view.x = valueAnimator.animatedValue as Float
                }
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        viewPager.currentItem = position
                        tv_one.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                        tv_second.setTextColor(ContextCompat.getColor(mContext, R.color.colorTitle))
                        tv_third.setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.colorTitle
                            )
                        )
                        animFinish = true
                    }
                })
            }
            1 -> {
                animator = ValueAnimator.ofFloat(view.x, tv_second.x)
                animator!!.setTarget(tv_second)
                animator.addUpdateListener { valueAnimator ->
                    view.x = valueAnimator.animatedValue as Float
                }
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        viewPager.currentItem = position
                        tv_one.setTextColor(ContextCompat.getColor(mContext, R.color.colorTitle))
                        tv_second.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                        tv_third.setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.colorTitle
                            )
                        )
                        animFinish = true
                    }
                })
            }
            2 -> {
                animator = ValueAnimator.ofFloat(view.x, tv_third.x)
                animator!!.setTarget(tv_third)
                animator.addUpdateListener { valueAnimator ->
                    view.x = valueAnimator.animatedValue as Float
                }
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        viewPager.currentItem = position
                        tv_one.setTextColor(ContextCompat.getColor(mContext, R.color.colorTitle))
                        tv_second.setTextColor(ContextCompat.getColor(mContext, R.color.colorTitle))
                        tv_third.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                        animFinish = true
                    }
                })
            }
            else -> {
            }
        }
        if (animator != null) {
            animator.duration = 200
            animator.interpolator = AccelerateInterpolator()
            animator.start()
        }
    }

    fun setOnTabBtnClickListener(listener: OnTabClickListener?) {
        this.mBtnClickListener = listener
    }

    override fun onClick(v: View) {
        if (this.mBtnClickListener != null) {
            when (v) {
                this.tv_one -> this.mBtnClickListener!!.onTabBtnClick(CommonTabBtn.ONE, v)
                this.tv_second -> this.mBtnClickListener!!.onTabBtnClick(CommonTabBtn.TWO, v)
                this.tv_third -> this.mBtnClickListener!!.onTabBtnClick(CommonTabBtn.THREE, v)
            }
        }

    }

    enum class CommonTabBtn() {
        ONE,
        TWO,
        THREE
    }

    interface OnTabClickListener {
        fun onTabBtnClick(var1: CommonTabBtn, var2: View)
    }
}
