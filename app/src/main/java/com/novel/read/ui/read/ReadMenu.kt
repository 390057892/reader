package com.novel.read.ui.read

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.novel.read.App
import com.novel.read.R
import com.novel.read.constant.PreferKey
import com.novel.read.help.AppConfig
import com.novel.read.help.ReadBookConfig
import com.novel.read.lib.Selector
import com.novel.read.service.help.ReadBook
import com.novel.read.utils.AnimationUtilsSupport
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.SystemUtils
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.view_read_menu.view.*
import org.jetbrains.anko.sdk27.listeners.onClick
import org.jetbrains.anko.sdk27.listeners.onLongClick

/**
 * 阅读界面菜单
 */
class ReadMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    var cnaShowMenu: Boolean = false
    private val callBack: CallBack? get() = activity as? CallBack
    private lateinit var menuTopIn: Animation
    private lateinit var menuTopOut: Animation
    private lateinit var menuBottomIn: Animation
    private lateinit var menuBottomOut: Animation
    private val bgColor: Int = context.bottomBackground
    private val textColor: Int = context.getPrimaryTextColor(ColorUtils.isColorLight(bgColor))
    private val bottomBackgroundList: ColorStateList = Selector.colorBuild()
        .setDefaultColor(bgColor)
        .setPressedColor(ColorUtils.darkenColor(bgColor))
        .create()
    private var onMenuOutEnd: (() -> Unit)? = null
    val showBrightnessView get() = context.getPrefBoolean(PreferKey.showBrightnessView, true)

    init {
        inflate(context, R.layout.view_read_menu, this)
        if (AppConfig.isNightTheme) {
            fabNightTheme.setImageResource(R.drawable.ic_daytime)
        } else {
            fabNightTheme.setImageResource(R.drawable.ic_brightness)
        }
        initAnimation()
        val brightnessBackground = GradientDrawable()
        brightnessBackground.cornerRadius = 5F.dp
        brightnessBackground.setColor(ColorUtils.adjustAlpha(bgColor, 0.5f))
        ll_bottom_bg.setBackgroundColor(bgColor)
        fabSearch.backgroundTintList = bottomBackgroundList
        fabSearch.setColorFilter(textColor)
        fabNightTheme.backgroundTintList = bottomBackgroundList
        fabNightTheme.setColorFilter(textColor)
        tv_pre.setTextColor(textColor)
        tv_next.setTextColor(textColor)
        iv_catalog.setColorFilter(textColor)
        tv_catalog.setTextColor(textColor)
        iv_read_aloud.setColorFilter(textColor)
        tv_read_aloud.setTextColor(textColor)
        iv_font.setColorFilter(textColor)
        tv_font.setTextColor(textColor)
        iv_setting.setColorFilter(textColor)
        tv_setting.setTextColor(textColor)
        vw_bg.onClick { }
        vwNavigationBar.onClick { }
        bindEvent()
    }

    /**
     * 设置屏幕亮度
     */
    private fun setScreenBrightness(value: Int) {
        var brightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        if (!brightnessAuto()) {
            brightness = value.toFloat()
            if (brightness < 1f) brightness = 1f
            brightness /= 255f
        }
        val params = activity?.window?.attributes
        params?.screenBrightness = brightness
        activity?.window?.attributes = params
    }

    fun runMenuIn() {
        this.visible()
        title_bar.visible()
        bottom_menu.visible()
        title_bar.startAnimation(menuTopIn)
        bottom_menu.startAnimation(menuBottomIn)
    }

    fun runMenuOut(onMenuOutEnd: (() -> Unit)? = null) {
        this.onMenuOutEnd = onMenuOutEnd
        if (this.isVisible) {
            title_bar.startAnimation(menuTopOut)
            bottom_menu.startAnimation(menuBottomOut)
        }
    }

    private fun brightnessAuto(): Boolean {
        return context.getPrefBoolean("brightnessAuto", true) || !showBrightnessView
    }

    private fun bindEvent() {

        //阅读进度
        seek_read_page.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                ReadBook.skipToPage(seekBar.progress)
            }
        })

        //搜索
        fabSearch.onClick {
            runMenuOut {
                callBack?.openSearchActivity(null)
            }
        }

        //夜间模式
        fabNightTheme.onClick {
            AppConfig.isNightTheme = !AppConfig.isNightTheme
            App.INSTANCE.applyDayNight()
        }

        //上一章
        tv_pre.onClick { ReadBook.moveToPrevChapter(upContent = true, toLast = false) }

        //下一章
        tv_next.onClick { ReadBook.moveToNextChapter(true) }

        //目录
        ll_catalog.onClick {
            runMenuOut {
                callBack?.openChapterList()
            }
        }

        //朗读
        ll_read_aloud.onClick {
            runMenuOut {
                callBack?.onClickReadAloud()
            }
        }
        ll_read_aloud.onLongClick {
            runMenuOut { callBack?.showReadAloudDialog() }
            true
        }
        //界面
        ll_font.onClick {
            runMenuOut {
                callBack?.showAdjust()
            }
        }

        //设置
        ll_setting.onClick {
            runMenuOut {
                callBack?.showReadStyle()
            }
        }
    }

    private fun initAnimation() {
        //显示菜单
        menuTopIn = AnimationUtilsSupport.loadAnimation(context, R.anim.anim_readbook_top_in)
        menuBottomIn = AnimationUtilsSupport.loadAnimation(context, R.anim.anim_readbook_bottom_in)
        menuTopIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                callBack?.upSystemUiVisibility()
            }

            override fun onAnimationEnd(animation: Animation) {
                vw_menu_bg.onClick { runMenuOut() }
                vwNavigationBar.layoutParams = vwNavigationBar.layoutParams.apply {
                    height =
                        if (ReadBookConfig.hideNavigationBar
                            && SystemUtils.isNavigationBarExist(activity)
                        )
                            context.navigationBarHeight
                        else 0
                }
            }

            override fun onAnimationRepeat(animation: Animation) = Unit
        })

        //隐藏菜单
        menuTopOut = AnimationUtilsSupport.loadAnimation(context, R.anim.anim_readbook_top_out)
        menuBottomOut =
            AnimationUtilsSupport.loadAnimation(context, R.anim.anim_readbook_bottom_out)
        menuTopOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                vw_menu_bg.setOnClickListener(null)
            }

            override fun onAnimationEnd(animation: Animation) {
                this@ReadMenu.invisible()
                title_bar.invisible()
                bottom_menu.invisible()
                cnaShowMenu = false
                onMenuOutEnd?.invoke()
                callBack?.upSystemUiVisibility()
            }

            override fun onAnimationRepeat(animation: Animation) = Unit
        })
    }



    interface CallBack {
        fun autoPage()
        fun openReplaceRule()
        fun openChapterList()
        fun openSearchActivity(searchWord: String?)
        fun showAdjust()
        fun showReadStyle()
        fun showReadAloudDialog()
        fun upSystemUiVisibility()
        fun onClickReadAloud()
    }

}
