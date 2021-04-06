package com.novel.read.constant

import android.annotation.SuppressLint
import android.graphics.Color
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
object AppConst {

    const val APP_TAG = "TuZi"
    const val AppId = "20210306161"
    const val AppSecret = "c14f6a2b3dcah893a31b9i4f40f5cc08"

    const val channelIdDownload = "channel_download"
    const val channelIdReadAloud = "channel_read_aloud"

    val timeFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("HH:mm")
    }

    val dateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy/MM/dd HH:mm")
    }

    val fileNameFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yy-MM-dd-HH-mm-ss")
    }

    val keyboardToolChars: List<String> by lazy {
        arrayListOf(
            "※", "@", "&", "|", "%", "/", ":", "[", "]", "{", "}", "<", ">", "\\",
            "$", "#", "!", ".", "href", "src", "textNodes", "xpath", "json", "css",
            "id", "class", "tag"
        )
    }

    const val notificationIdRead = 1144771
    const val notificationIdAudio = 1144772
    const val notificationIdWeb = 1144773
    const val notificationIdDownload = 1144774


    const val refresh = 1
    const val loading = 2
    const val complete = 3
    const val error = 4
    const val loadMore = 5
    const val loadComplete = 6
    const val loadMoreFail = 7
    const val noMore = 8

    @kotlin.jvm.JvmField
    val tagColors = intArrayOf(
        Color.parseColor("#90C5F0"),
        Color.parseColor("#91CED5"),
        Color.parseColor("#F88F55"),
        Color.parseColor("#C0AFD0"),
        Color.parseColor("#E78F8F"),
        Color.parseColor("#67CCB7"),
        Color.parseColor("#F6BC7E"),
        Color.parseColor("#90C5F0"),
        Color.parseColor("#91CED5")
    )

    const val home = 0
    const val man = 1
    const val woman = 2

    const val shellName = "001"

    val menuViewNames = arrayOf(
        "com.android.internal.view.menu.ListMenuItemView",
        "androidx.appcompat.view.menu.ListMenuItemView"
    )
}