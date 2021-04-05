package com.novel.read.help

import android.content.Context
import android.graphics.Color
import androidx.annotation.Keep
import com.novel.read.App
import com.novel.read.R
import com.novel.read.constant.EventBus
import com.novel.read.constant.PreferKey
import com.novel.read.lib.ThemeStore
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.FileUtils
import com.novel.read.utils.ext.*
import java.io.File

object ThemeConfig {
    const val configFileName = "themeConfig.json"
    val configFilePath = FileUtils.getPath(App.INSTANCE.filesDir, configFileName)

    val configList: ArrayList<Config> by lazy {
        val cList = getConfigs() ?: DefaultData.defaultThemeConfigs
        ArrayList(cList)
    }

    fun upConfig() {
        getConfigs()?.let {
            it.forEach { config ->
                addConfig(config)
            }
        }
    }

    fun save() {
        val json = GSON.toJson(configList)
        FileUtils.deleteFile(configFilePath)
        FileUtils.createFileIfNotExist(configFilePath).writeText(json)
    }

    fun delConfig(index: Int) {
        configList.removeAt(index)
        save()
    }

    fun addConfig(json: String): Boolean {
        GSON.fromJsonObject<Config>(json.trim { it < ' ' })?.let {
            addConfig(it)
            return true
        }
        return false
    }

    private fun addConfig(newConfig: Config) {
        configList.forEachIndexed { index, config ->
            if (newConfig.themeName == config.themeName) {
                configList[index] = newConfig
                return
            }
        }
        configList.add(newConfig)
        save()
    }

    private fun getConfigs(): List<Config>? {
        val configFile = File(configFilePath)
        if (configFile.exists()) {
            try {
                val json = configFile.readText()
                return GSON.fromJsonArray(json)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun applyConfig(context: Context, config: Config) {
        val primary = Color.parseColor(config.primaryColor)
        val accent = Color.parseColor(config.accentColor)
        val background = Color.parseColor(config.backgroundColor)
        val bBackground = Color.parseColor(config.bottomBackground)
        if (config.isNightTheme) {
            context.putPrefInt(PreferKey.cNPrimary, primary)
            context.putPrefInt(PreferKey.cNAccent, accent)
            context.putPrefInt(PreferKey.cNBackground, background)
            context.putPrefInt(PreferKey.cNBBackground, bBackground)
        } else {
            context.putPrefInt(PreferKey.cPrimary, primary)
            context.putPrefInt(PreferKey.cAccent, accent)
            context.putPrefInt(PreferKey.cBackground, background)
            context.putPrefInt(PreferKey.cBBackground, bBackground)
        }
        AppConfig.isNightTheme = config.isNightTheme
        App.INSTANCE.applyDayNight()
        postEvent(EventBus.RECREATE, "")
    }

    fun saveDayTheme(context: Context, name: String) {
        val primary =
            context.getPrefInt(PreferKey.cPrimary, context.getCompatColor(R.color.md_brown_500))
        val accent =
            context.getPrefInt(PreferKey.cAccent, context.getCompatColor(R.color.md_red_600))
        val background =
            context.getPrefInt(PreferKey.cBackground, context.getCompatColor(R.color.md_grey_100))
        val bBackground =
            context.getPrefInt(PreferKey.cBBackground, context.getCompatColor(R.color.md_grey_200))
        val config = Config(
            themeName = name,
            isNightTheme = false,
            primaryColor = "#${primary.hexString}",
            accentColor = "#${accent.hexString}",
            backgroundColor = "#${background.hexString}",
            bottomBackground = "#${bBackground.hexString}"
        )
        addConfig(config)
    }

    fun saveNightTheme(context: Context, name: String) {
        val primary =
            context.getPrefInt(
                PreferKey.cNPrimary,
                context.getCompatColor(R.color.md_blue_grey_600)
            )
        val accent =
            context.getPrefInt(
                PreferKey.cNAccent,
                context.getCompatColor(R.color.md_deep_orange_800)
            )
        val background =
            context.getPrefInt(PreferKey.cNBackground, context.getCompatColor(R.color.md_grey_900))
        val bBackground =
            context.getPrefInt(PreferKey.cNBBackground, context.getCompatColor(R.color.md_grey_850))
        val config = Config(
            themeName = name,
            isNightTheme = true,
            primaryColor = "#${primary.hexString}",
            accentColor = "#${accent.hexString}",
            backgroundColor = "#${background.hexString}",
            bottomBackground = "#${bBackground.hexString}"
        )
        addConfig(config)
    }

    /**
     * 更新主题
     */
    fun applyTheme(context: Context) = with(context) {
        when {
            AppConfig.isEInkMode -> {
                ThemeStore.editTheme(this)
                    .coloredNavigationBar(true)
                    .primaryColor(Color.WHITE)
                    .accentColor(Color.BLACK)
                    .backgroundColor(Color.WHITE)
                    .bottomBackground(Color.WHITE)
                    .apply()
            }
            AppConfig.isNightTheme -> {
                val primary =
                    getPrefInt(PreferKey.cNPrimary, getCompatColor(R.color.md_grey_900))
                val accent =
                    getPrefInt(PreferKey.cNAccent, getCompatColor(R.color.md_amber_800))
                var background =
                    getPrefInt(PreferKey.cNBackground, getCompatColor(R.color.md_grey_900))
                if (ColorUtils.isColorLight(background)) {
                    background = getCompatColor(R.color.md_grey_900)
                    putPrefInt(PreferKey.cNBackground, background)
                }
                val bBackground =
                    getPrefInt(PreferKey.cNBBackground, getCompatColor(R.color.md_grey_900))
                ThemeStore.editTheme(this)
                    .coloredNavigationBar(true)
                    .primaryColor(ColorUtils.withAlpha(primary, 1f))
                    .accentColor(ColorUtils.withAlpha(accent, 1f))
                    .backgroundColor(ColorUtils.withAlpha(background, 1f))
                    .bottomBackground(ColorUtils.withAlpha(bBackground, 1f))
                    .apply()
            }
            else -> {
                val primary =
                    getPrefInt(PreferKey.cPrimary, getCompatColor(R.color.md_grey_50))
                val accent =
                    getPrefInt(PreferKey.cAccent, getCompatColor(R.color.md_amber_800))
                var background =
                    getPrefInt(PreferKey.cBackground, getCompatColor(R.color.md_grey_50))
                if (!ColorUtils.isColorLight(background)) {
                    background = getCompatColor(R.color.md_grey_50)
                    putPrefInt(PreferKey.cBackground, background)
                }
                val bBackground =
                    getPrefInt(PreferKey.cBBackground, getCompatColor(R.color.white))
                ThemeStore.editTheme(this)
                    .coloredNavigationBar(true)
                    .primaryColor(ColorUtils.withAlpha(primary, 1f))
                    .accentColor(ColorUtils.withAlpha(accent, 1f))
                    .backgroundColor(ColorUtils.withAlpha(background, 1f))
                    .bottomBackground(ColorUtils.withAlpha(bBackground, 1f))
                    .apply()
            }
        }
    }

    @Keep
    class Config(
        var themeName: String,
        var isNightTheme: Boolean,
        var primaryColor: String,
        var accentColor: String,
        var backgroundColor: String,
        var bottomBackground: String
    )

}