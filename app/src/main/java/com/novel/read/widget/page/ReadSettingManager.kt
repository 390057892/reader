package com.novel.read.widget.page


import com.novel.read.utlis.DialogUtils
import com.novel.read.utlis.ScreenUtils
import com.novel.read.utlis.SpUtil

class ReadSettingManager private constructor() {

    var brightness: Int
        get() = SpUtil.getIntValue(SHARED_READ_BRIGHTNESS, 40)
        set(progress) = SpUtil.setIntValue(SHARED_READ_BRIGHTNESS, progress)

    val isBrightnessAuto: Boolean
        get() = SpUtil.getBooleanValue(SHARED_READ_IS_BRIGHTNESS_AUTO, false)

    var textSize: Int
        get() = SpUtil.getIntValue(SHARED_READ_TEXT_SIZE, ScreenUtils.spToPx(16))
        set(textSize) = SpUtil.setIntValue(SHARED_READ_TEXT_SIZE, textSize)

    var isDefaultTextSize: Boolean
        get() = SpUtil.getBooleanValue(SHARED_READ_IS_TEXT_DEFAULT, false)
        set(isDefault) = SpUtil.setBooleanValue(SHARED_READ_IS_TEXT_DEFAULT, isDefault)

    var pageMode: PageMode
        get() {
            val mode = SpUtil.getIntValue(SHARED_READ_PAGE_MODE, PageMode.SIMULATION.ordinal)
            return PageMode.values()[mode]
        }
        set(mode) = SpUtil.setIntValue(SHARED_READ_PAGE_MODE, mode.ordinal)

    var pageStyle: PageStyle
        get() {
            val style = SpUtil.getIntValue(SHARED_READ_BG, PageStyle.BG_0.ordinal)
            return PageStyle.values()[style]
        }
        set(pageStyle) = SpUtil.setIntValue(SHARED_READ_BG, pageStyle.ordinal)

    var isNightMode: Boolean
        get() = SpUtil.getBooleanValue(SHARED_READ_NIGHT_MODE, false)
        set(isNight) = SpUtil.setBooleanValue(SHARED_READ_NIGHT_MODE, isNight)

    var isVolumeTurnPage: Boolean
        get() = SpUtil.getBooleanValue(SHARED_READ_VOLUME_TURN_PAGE, false)
        set(isTurn) = SpUtil.setBooleanValue(SHARED_READ_VOLUME_TURN_PAGE, isTurn)

    var isFullScreen: Boolean
        get() = SpUtil.getBooleanValue(SHARED_READ_FULL_SCREEN, false)
        set(isFullScreen) = SpUtil.setBooleanValue(SHARED_READ_FULL_SCREEN, isFullScreen)

    var convertType: Int
        get() = SpUtil.getIntValue(SHARED_READ_CONVERT_TYPE, 1)
        set(convertType) = SpUtil.setIntValue(SHARED_READ_CONVERT_TYPE, convertType)

    fun setAutoBrightness(isAuto: Boolean) {
        SpUtil.setBooleanValue(SHARED_READ_IS_BRIGHTNESS_AUTO, isAuto)
    }

    companion object {
        /*************实在想不出什么好记的命名方式。。 */
        val READ_BG_DEFAULT = 0
        val READ_BG_1 = 1
        val READ_BG_2 = 2
        val READ_BG_3 = 3
        val READ_BG_4 = 4
        val NIGHT_MODE = 5

        val SHARED_READ_BG = "shared_read_bg"
        val SHARED_READ_BRIGHTNESS = "shared_read_brightness"
        val SHARED_READ_IS_BRIGHTNESS_AUTO = "shared_read_is_brightness_auto"
        val SHARED_READ_TEXT_SIZE = "shared_read_text_size"
        val SHARED_READ_IS_TEXT_DEFAULT = "shared_read_text_default"
        val SHARED_READ_PAGE_MODE = "shared_read_mode"
        val SHARED_READ_NIGHT_MODE = "shared_night_mode"
        val SHARED_READ_VOLUME_TURN_PAGE = "shared_read_volume_turn_page"
        val SHARED_READ_FULL_SCREEN = "shared_read_full_screen"
        val SHARED_READ_CONVERT_TYPE = "shared_read_convert_type"

//        @Volatile
//        private var sInstance: ReadSettingManager? = null
//
//        val instance: ReadSettingManager?
//            get() {
//                if (sInstance == null) {
//                    synchronized(ReadSettingManager::class.java) {
//                        if (sInstance == null) {
//                            sInstance = ReadSettingManager()
//                        }
//                    }
//                }
//                return sInstance
//            }
        @Volatile
        private var instance: ReadSettingManager? = null

        @Synchronized
        fun getInstance(): ReadSettingManager {
            if (instance == null) {
                instance = ReadSettingManager()
            }
            return instance as ReadSettingManager
        }
    }
}
