package com.novel.read.ui.read

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.novel.read.App
import com.novel.read.R
import com.novel.read.constant.PreferKey
import com.novel.read.databinding.ViewReadMenuBinding
import com.novel.read.help.AppConfig
import com.novel.read.help.ReadBookConfig
import com.novel.read.lib.Selector
import com.novel.read.service.help.ReadBook
import com.novel.read.utils.AnimationUtilsSupport
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.SystemUtils
import com.novel.read.utils.ext.*
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
    private val callBack: CallBack get() = activity as CallBack
    private val binding = ViewReadMenuBinding.inflate(LayoutInflater.from(context), this, true)
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
        if (AppConfig.isNightTheme) {
            binding.fabNightTheme.setImageResource(R.drawable.ic_daytime)
        } else {
            binding.fabNightTheme.setImageResource(R.drawable.ic_brightness)
        }
        initAnimation()
        val brightnessBackground = GradientDrawable()
        brightnessBackground.cornerRadius = 5F.dp
        brightnessBackground.setColor(ColorUtils.adjustAlpha(bgColor, 0.5f))
        binding.llBottomBg.setBackgroundColor(bgColor)
        binding.fabSearch.backgroundTintList = bottomBackgroundList
        binding.fabSearch.setColorFilter(textColor)
        binding.fabNightTheme.backgroundTintList = bottomBackgroundList
        binding.fabNightTheme.setColorFilter(textColor)
        binding.tvPre.setTextColor(textColor)
        binding.tvNext.setTextColor(textColor)
        binding.ivCatalog.setColorFilter(textColor)
        binding.tvCatalog.setTextColor(textColor)
        binding.ivReadAloud.setColorFilter(textColor)
        binding.tvReadAloud.setTextColor(textColor)
        binding.ivFont.setColorFilter(textColor)
        binding.tvFont.setTextColor(textColor)
        binding.ivSetting.setColorFilter(textColor)
        binding.tvSetting.setTextColor(textColor)
        binding.vwBg.onClick { }
        binding.vwNavigationBar.onClick { }
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
        binding.titleBar.visible()
        binding.bottomMenu.visible()
        binding.titleBar.startAnimation(menuTopIn)
        binding.bottomMenu.startAnimation(menuBottomIn)
    }

    fun runMenuOut(onMenuOutEnd: (() -> Unit)? = null) {
        this.onMenuOutEnd = onMenuOutEnd
        if (this.isVisible) {
            binding.titleBar.startAnimation(menuTopOut)
            binding.bottomMenu.startAnimation(menuBottomOut)
        }
    }

    private fun brightnessAuto(): Boolean {
        return context.getPrefBoolean("brightnessAuto", true) || !showBrightnessView
    }

    private fun bindEvent() = with(binding){
        tvChapterName.onClick {

        }
        //阅读进度
        seekReadPage.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
                callBack.openSearchActivity(null)
            }
        }

        //夜间模式
        fabNightTheme.onClick {
            AppConfig.isNightTheme = !AppConfig.isNightTheme
            App.INSTANCE.applyDayNight()
        }

        //上一章
        tvPre.onClick { ReadBook.moveToPrevChapter(upContent = true, toLast = false) }

        //下一章
        tvNext.onClick { ReadBook.moveToNextChapter(true) }

        //目录
        llCatalog.onClick {
            runMenuOut {
                callBack.openChapterList()
            }
        }

        //朗读
        llReadAloud.onClick {
            runMenuOut {
                callBack.onClickReadAloud()
            }
        }
        llReadAloud.onLongClick {
            runMenuOut { callBack.showReadAloudDialog() }
            true
        }
        //界面
        llFont.onClick {
            runMenuOut {
                callBack.showAdjust()
            }
        }

        //设置
        llSetting.onClick {
            runMenuOut {
                callBack.showReadStyle()
            }
        }
    }

    private fun initAnimation() {
        //显示菜单
        menuTopIn = AnimationUtilsSupport.loadAnimation(context, R.anim.anim_readbook_top_in)
        menuBottomIn = AnimationUtilsSupport.loadAnimation(context, R.anim.anim_readbook_bottom_in)
        menuTopIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                callBack.upSystemUiVisibility()
            }

            override fun onAnimationEnd(animation: Animation) {
                binding.vwMenuBg.onClick { runMenuOut() }
                binding.vwNavigationBar.layoutParams = binding.vwNavigationBar.layoutParams.apply {
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
                binding.vwMenuBg.setOnClickListener(null)
            }

            override fun onAnimationEnd(animation: Animation) {
                this@ReadMenu.invisible()
                binding.titleBar.invisible()
                binding.bottomMenu.invisible()
                cnaShowMenu = false
                onMenuOutEnd?.invoke()
                callBack.upSystemUiVisibility()
            }

            override fun onAnimationRepeat(animation: Animation) = Unit
        })
    }


    fun setTitle(title: String) {
        binding.titleBar.title = title
    }

    fun upBookView() {
        ReadBook.curTextChapter?.let {
            binding.tvChapterName.text = it.title
            binding.tvChapterName.visible()
            binding.seekReadPage.max = it.pageSize.minus(1)
            binding.seekReadPage.progress = ReadBook.durPageIndex
            binding.tvPre.isEnabled = ReadBook.durChapterIndex != 0
            binding.tvNext.isEnabled = ReadBook.durChapterIndex != ReadBook.chapterSize - 1
        } ?: let {
            binding.tvChapterName.gone()
        }
    }

    fun setSeekPage(seek: Int) {
        binding.seekReadPage.progress = seek
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
