package com.novel.read.help

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import com.novel.read.App
import com.novel.read.R
import com.novel.read.constant.PreferKey
import com.novel.read.user.VipHelper
import com.novel.read.utils.BitmapUtils
import com.novel.read.utils.FileUtils
import com.novel.read.utils.ext.*
import com.novel.read.help.coroutine.Coroutine
import io.legado.app.ui.book.read.page.provider.ChapterProvider
import java.io.File

/**
 * 阅读界面配置
 */
@Keep
object ReadBookConfig {
    const val configFileName = "readConfig.json"
    const val shareConfigFileName = "shareReadConfig.json"
    val configFilePath = FileUtils.getPath(App.INSTANCE.filesDir, configFileName)
    val shareConfigFilePath = FileUtils.getPath(App.INSTANCE.filesDir, shareConfigFileName)
    val configList: ArrayList<Config> = arrayListOf()
    lateinit var shareConfig: Config
    var durConfig
        get() = getConfig(styleSelect)
        set(value) {
            configList[styleSelect] = value
            if (shareLayout) {
                shareConfig = value
            }
            upBg()
        }

    var bg: Drawable? = null
    var bgMeanColor: Int = 0
    val textColor: Int get() = durConfig.curTextColor()

    init {
        initConfigs()
        initShareConfig()
    }

    @Synchronized
    fun getConfig(index: Int): Config {
        if (configList.size < 5) {
            resetAll()
        }
        return configList.getOrNull(index) ?: configList[0]
    }

    fun initConfigs() {
        val configFile = File(configFilePath)
        var configs: List<Config>? = null
        if (configFile.exists()) {
            try {
                val json = configFile.readText()
                configs = GSON.fromJsonArray(json)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        (configs ?: DefaultData.defaultReadConfigs).let {
            configList.clear()
            configList.addAll(it)
        }
    }

    fun initShareConfig() {
        val configFile = File(shareConfigFilePath)
        var c: Config? = null
        if (configFile.exists()) {
            try {
                val json = configFile.readText()
                c = GSON.fromJsonObject(json)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        shareConfig = c ?: configList.getOrNull(5) ?: Config()
    }

    fun upBg() {
        val resources = App.INSTANCE.resources
        val dm = resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        bg = durConfig.curBgDrawable(width, height).apply {
            if (this is BitmapDrawable) {
                bgMeanColor = BitmapUtils.getMeanColor(bitmap)
            } else if (this is ColorDrawable) {
                bgMeanColor = color
            }
        }
    }

    fun save() {
        Coroutine.async {
            synchronized(this) {
                GSON.toJson(configList).let {
                    FileUtils.deleteFile(configFilePath)
                    FileUtils.createFileIfNotExist(configFilePath).writeText(it)
                }
                GSON.toJson(shareConfig).let {
                    FileUtils.deleteFile(shareConfigFilePath)
                    FileUtils.createFileIfNotExist(shareConfigFilePath).writeText(it)
                }
            }
        }
    }

    fun deleteDur(): Boolean {
        if (configList.size > 5) {
            configList.removeAt(styleSelect)
            if (styleSelect > 0) {
                styleSelect -= 1
            }
            upBg()
            return true
        }
        return false
    }

    private fun resetAll() {
        DefaultData.defaultReadConfigs.let {
            configList.clear()
            configList.addAll(it)
            save()
        }
    }

    //配置写入读取
    var autoReadSpeed = App.INSTANCE.getPrefInt(PreferKey.autoReadSpeed, 46)
        set(value) {
            field = value
            App.INSTANCE.putPrefInt(PreferKey.autoReadSpeed, value)
        }
    var styleSelect = App.INSTANCE.getPrefInt(PreferKey.readStyleSelect)
        set(value) {
            field = value
            if (App.INSTANCE.getPrefInt(PreferKey.readStyleSelect) != value) {
                App.INSTANCE.putPrefInt(PreferKey.readStyleSelect, value)
            }
        }
    var shareLayout = App.INSTANCE.getPrefBoolean(PreferKey.shareLayout, true)
        set(value) {
            field = value
            if (App.INSTANCE.getPrefBoolean(PreferKey.shareLayout) != value) {
                App.INSTANCE.putPrefBoolean(PreferKey.shareLayout, value)
            }
        }
    val clickTurnPage get() = App.INSTANCE.getPrefBoolean(PreferKey.clickTurnPage, true)
    val clickAllNext get() = App.INSTANCE.getPrefBoolean(PreferKey.clickAllNext, false)
    val textFullJustify get() = App.INSTANCE.getPrefBoolean(PreferKey.textFullJustify, true)
    val textBottomJustify get() = App.INSTANCE.getPrefBoolean(PreferKey.textBottomJustify, true)
    var hideStatusBar = App.INSTANCE.getPrefBoolean(PreferKey.hideStatusBar)
    var hideNavigationBar = App.INSTANCE.getPrefBoolean(PreferKey.hideNavigationBar)

    val config get() = if (shareLayout) shareConfig else durConfig

    var pageAnim: Int
        get() = config.curPageAnim()
        set(value) {
            config.setCurPageAnim(value)
        }

    var textFont: String
        get() = config.textFont
        set(value) {
            config.textFont = value
        }

    var textBold: Int
        get() = config.textBold
        set(value) {
            config.textBold = value
        }

    var textSize: Int
        get() = config.textSize
        set(value) {
            config.textSize = value
        }

    var letterSpacing: Float
        get() = config.letterSpacing
        set(value) {
            config.letterSpacing = value
        }

    var lineSpacingExtra: Int
        get() = config.lineSpacingExtra
        set(value) {
            config.lineSpacingExtra = value
        }

    var paragraphSpacing: Int
        get() = config.paragraphSpacing
        set(value) {
            config.paragraphSpacing = value
        }

    var titleMode: Int
        get() = config.titleMode
        set(value) {
            config.titleMode = value
        }
    var titleSize: Int
        get() = config.titleSize
        set(value) {
            config.titleSize = value
        }

    var titleTopSpacing: Int
        get() = config.titleTopSpacing
        set(value) {
            config.titleTopSpacing = value
        }

    var titleBottomSpacing: Int
        get() = config.titleBottomSpacing
        set(value) {
            config.titleBottomSpacing = value
        }

    var paragraphIndent: String
        get() = config.paragraphIndent
        set(value) {
            config.paragraphIndent = value
        }

    var paddingBottom: Int
//        get() = config.paddingBottom
        get() = if (VipHelper.showAd()) {
            10
        } else {
            20
        }
        set(value) {
            config.paddingBottom = value
        }

    var paddingLeft: Int
        get() = config.paddingLeft
        set(value) {
            config.paddingLeft = value
        }

    var paddingRight: Int
        get() = config.paddingRight
        set(value) {
            config.paddingRight = value
        }

    var paddingTop: Int
        get() = config.paddingTop
        set(value) {
            config.paddingTop = value
        }

    var headerPaddingBottom: Int
        get() = config.headerPaddingBottom
        set(value) {
            config.headerPaddingBottom = value
        }

    var headerPaddingLeft: Int
        get() = config.headerPaddingLeft
        set(value) {
            config.headerPaddingLeft = value
        }

    var headerPaddingRight: Int
        get() = config.headerPaddingRight
        set(value) {
            config.headerPaddingRight = value
        }

    var headerPaddingTop: Int
        get() = config.headerPaddingTop
        set(value) {
            config.headerPaddingTop = value
        }

    var footerPaddingBottom: Int
        //        get() = config.footerPaddingBottom
        get() = if (VipHelper.showAd()) {
            60
        } else {
            10
        }
        set(value) {
            config.footerPaddingBottom = value
        }

    var footerPaddingLeft: Int
        get() = config.footerPaddingLeft
        set(value) {
            config.footerPaddingLeft = value
        }

    var footerPaddingRight: Int
        get() = config.footerPaddingRight
        set(value) {
            config.footerPaddingRight = value
        }

    var footerPaddingTop: Int
        get() = config.footerPaddingTop
        set(value) {
            config.footerPaddingTop = value
        }

    var showHeaderLine: Boolean
        get() = config.showHeaderLine
        set(value) {
            config.showHeaderLine = value
        }

    var showFooterLine: Boolean
        get() = config.showFooterLine
        set(value) {
            config.showFooterLine = value
        }

    fun getExportConfig(): Config {
        val exportConfig = GSON.fromJsonObject<Config>(GSON.toJson(durConfig))!!
        if (shareLayout) {
            exportConfig.textFont = shareConfig.textFont
            exportConfig.textBold = shareConfig.textBold
            exportConfig.textSize = shareConfig.textSize
            exportConfig.letterSpacing = shareConfig.letterSpacing
            exportConfig.lineSpacingExtra = shareConfig.lineSpacingExtra
            exportConfig.paragraphSpacing = shareConfig.paragraphSpacing
            exportConfig.titleMode = shareConfig.titleMode
            exportConfig.titleSize = shareConfig.titleSize
            exportConfig.titleTopSpacing = shareConfig.titleTopSpacing
            exportConfig.titleBottomSpacing = shareConfig.titleBottomSpacing
            exportConfig.paddingBottom = shareConfig.paddingBottom
            exportConfig.paddingLeft = shareConfig.paddingLeft
            exportConfig.paddingRight = shareConfig.paddingRight
            exportConfig.paddingTop = shareConfig.paddingTop
            exportConfig.headerPaddingBottom = shareConfig.headerPaddingBottom
            exportConfig.headerPaddingLeft = shareConfig.headerPaddingLeft
            exportConfig.headerPaddingRight = shareConfig.headerPaddingRight
            exportConfig.headerPaddingTop = shareConfig.headerPaddingTop
            exportConfig.footerPaddingBottom = shareConfig.footerPaddingBottom
            exportConfig.footerPaddingLeft = shareConfig.footerPaddingLeft
            exportConfig.footerPaddingRight = shareConfig.footerPaddingRight
            exportConfig.footerPaddingTop = shareConfig.footerPaddingTop
            exportConfig.showHeaderLine = shareConfig.showHeaderLine
            exportConfig.showFooterLine = shareConfig.showFooterLine
            exportConfig.tipHeaderLeft = shareConfig.tipHeaderLeft
            exportConfig.tipHeaderMiddle = shareConfig.tipHeaderMiddle
            exportConfig.tipHeaderRight = shareConfig.tipHeaderRight
            exportConfig.tipFooterLeft = shareConfig.tipFooterLeft
            exportConfig.tipFooterMiddle = shareConfig.tipFooterMiddle
            exportConfig.tipFooterRight = shareConfig.tipFooterRight
            exportConfig.hideHeader = shareConfig.hideHeader
            exportConfig.hideFooter = shareConfig.hideFooter
        }
        return exportConfig
    }

    @Keep
    class Config(
        var name: String = "",
        var bgStr: String = "#EFEFF7",//白天背景
        var bgStrNight: String = "#000000",//夜间背景
        var bgStrEInk: String = "#FFFFFF",
        var bgType: Int = 0,//白天背景类型 0:颜色, 1:assets图片, 2其它图片
        var bgTypeNight: Int = 0,//夜间背景类型
        var bgTypeEInk: Int = 0,
        private var darkStatusIcon: Boolean = true,//白天是否暗色状态栏
        private var darkStatusIconNight: Boolean = false,//晚上是否暗色状态栏
        private var darkStatusIconEInk: Boolean = true,
        private var textColor: String = "#383429",//白天文字颜色
        private var textColorNight: String = "#ADADAD",//夜间文字颜色
        private var textColorEInk: String = "#000000",
        private var pageAnim: Int = 0,
        private var pageAnimEInk: Int = 3,
        var textFont: String = "",//字体
        var textBold: Int = 0,//是否粗体字 0:正常, 1:粗体, 2:细体
        var textSize: Int = 20,//文字大小
        var letterSpacing: Float = 0.1f,//字间距
        var lineSpacingExtra: Int = 13,//行间距
        var paragraphSpacing: Int = 4,//段距
        var titleMode: Int = 0,//标题居中 1 居中
        var titleSize: Int = 5,
        var titleTopSpacing: Int = 12,
        var titleBottomSpacing: Int = 0,
        var paragraphIndent: String = "　　",//段落缩进
        var paddingBottom: Int = 6,
        var paddingLeft: Int = 16,
        var paddingRight: Int = 16,
        var paddingTop: Int = 10,
        var headerPaddingBottom: Int = 0,
        var headerPaddingLeft: Int = 16,
        var headerPaddingRight: Int = 16,
        var headerPaddingTop: Int = 0,
        var footerPaddingBottom: Int = 60,
        var footerPaddingLeft: Int = 16,
        var footerPaddingRight: Int = 16,
        var footerPaddingTop: Int = 6,
        var showHeaderLine: Boolean = false,
        var showFooterLine: Boolean = false,
        var tipHeaderLeft: Int = ReadTipConfig.time,
        var tipHeaderMiddle: Int = ReadTipConfig.none,
        var tipHeaderRight: Int = ReadTipConfig.battery,
        var tipFooterLeft: Int = ReadTipConfig.chapterTitle,
        var tipFooterMiddle: Int = ReadTipConfig.none,
        var tipFooterRight: Int = ReadTipConfig.pageAndTotal,
        var hideHeader: Boolean = true,
        var hideFooter: Boolean = false
    ) {

        fun setCurTextColor(color: Int) {
            when {
                AppConfig.isEInkMode -> textColorEInk = "#${color.hexString}"
                AppConfig.isNightTheme -> textColorNight = "#${color.hexString}"
                else -> textColor = "#${color.hexString}"
            }
            ChapterProvider.upStyle()
        }

        fun curTextColor(): Int {
            return when {
                AppConfig.isEInkMode -> Color.parseColor(textColorEInk)
                AppConfig.isNightTheme -> Color.parseColor(textColorNight)
                else -> Color.parseColor(textColor)
            }
        }

        fun setCurStatusIconDark(isDark: Boolean) {
            when {
                AppConfig.isEInkMode -> darkStatusIconEInk = isDark
                AppConfig.isNightTheme -> darkStatusIconNight = isDark
                else -> darkStatusIcon = isDark
            }
        }

        fun curStatusIconDark(): Boolean {
            return when {
                AppConfig.isEInkMode -> darkStatusIconEInk
                AppConfig.isNightTheme -> darkStatusIconNight
                else -> darkStatusIcon
            }
        }

        fun setCurPageAnim(anim: Int) {
            when {
                AppConfig.isEInkMode -> pageAnimEInk = anim
                else -> pageAnim = anim
            }
        }

        fun curPageAnim(): Int {
            return when {
                AppConfig.isEInkMode -> pageAnimEInk
                else -> pageAnim
            }
        }

        fun setCurBg(bgType: Int, bg: String) {
            when {
                AppConfig.isEInkMode -> {
                    bgTypeEInk = bgType
                    bgStrEInk = bg
                }
                AppConfig.isNightTheme -> {
                    bgTypeNight = bgType
                    bgStrNight = bg
                }
                else -> {
                    this.bgType = bgType
                    bgStr = bg
                }
            }
        }

        fun curBgStr(): String {
            return when {
                AppConfig.isEInkMode -> bgStrEInk
                AppConfig.isNightTheme -> bgStrNight
                else -> bgStr
            }
        }

        fun curBgType(): Int {
            return when {
                AppConfig.isEInkMode -> bgTypeEInk
                AppConfig.isNightTheme -> bgTypeNight
                else -> bgType
            }
        }

        fun curBgDrawable(width: Int, height: Int): Drawable {
            var bgDrawable: Drawable? = null
            val resources = App.INSTANCE.resources
            try {
                bgDrawable = when (curBgType()) {
                    0 -> ColorDrawable(Color.parseColor(curBgStr()))
                    1 -> {
                        BitmapDrawable(
                            resources,
                            BitmapUtils.decodeAssetsBitmap(
                                App.INSTANCE,
                                "bg" + File.separator + curBgStr(),
                                width,
                                height
                            )
                        )
                    }
                    else -> BitmapDrawable(
                        resources,
                        BitmapUtils.decodeBitmap(curBgStr(), width, height)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bgDrawable ?: ColorDrawable(App.INSTANCE.getCompatColor(R.color.background))
        }
    }
}