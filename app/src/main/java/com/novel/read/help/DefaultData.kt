package com.novel.read.help

import com.novel.read.App
import com.novel.read.data.db.entity.HttpTTS
import com.novel.read.utils.ext.*
import java.io.File

object DefaultData {

    const val httpTtsFileName = "httpTTS.json"
    const val txtTocRuleFileName = "txtTocRule.json"

    val defaultHttpTTS by lazy {
        val json =
            String(
                App.INSTANCE.assets.open("defaultData${File.separator}$httpTtsFileName")
                    .readBytes()
            )
        GSON.fromJsonArray<HttpTTS>(json)!!
    }

    val defaultReadConfigs by lazy {
        val json = String(
            App.INSTANCE.assets.open("defaultData${File.separator}${ReadBookConfig.configFileName}")
                .readBytes()
        )
        GSON.fromJsonArray<ReadBookConfig.Config>(json)!!
    }

    val defaultThemeConfigs by lazy {
        val json = String(
            App.INSTANCE.assets.open("defaultData${File.separator}${ThemeConfig.configFileName}")
                .readBytes()
        )
        GSON.fromJsonArray<ThemeConfig.Config>(json)!!
    }
}