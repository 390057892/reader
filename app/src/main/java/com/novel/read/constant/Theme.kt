package com.novel.read.constant

import com.novel.read.help.AppConfig
import com.novel.read.utils.ColorUtils


enum class Theme {
    Dark, Light, Auto, Transparent;

    companion object {
        fun getTheme() =
            if (AppConfig.isNightTheme) Dark
            else Light

        fun getTheme(backgroundColor: Int) =
            if (ColorUtils.isColorLight(backgroundColor)) Light
            else Dark
        
    }
}