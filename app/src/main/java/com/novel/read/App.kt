package com.novel.read

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.bumptech.glide.Glide
import com.jeremyliao.liveeventbus.LiveEventBus
import com.novel.read.constant.AppConst.channelIdDownload
import com.novel.read.constant.AppConst.channelIdReadAloud
import com.novel.read.constant.EventBus
import com.novel.read.data.db.BookDatabase
import com.novel.read.help.ActivityHelp
import com.novel.read.help.AppConfig
import com.novel.read.help.ReadBookConfig
import com.novel.read.help.ThemeConfig
import com.novel.read.utils.LanguageUtils
import com.novel.read.utils.ext.postEvent
import org.litepal.LitePal

@Suppress("DEPRECATION")
class App : MultiDexApplication() {

    companion object {
        @JvmStatic
        lateinit var INSTANCE: App
            private set

        @JvmStatic
        lateinit var db: BookDatabase
            private set

        lateinit var androidId: String
        var versionCode = 0
        var versionName = ""
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        androidId = Settings.System.getString(contentResolver, Settings.Secure.ANDROID_ID)
        LanguageUtils.setConfiguration(this)
        LitePal.initialize(this)
        db = BookDatabase.get()
        packageManager.getPackageInfo(packageName, 0)?.let {
            versionCode = it.versionCode
            versionName = it.versionName
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannelId()
        applyDayNight()
        LiveEventBus
            .config()
            .lifecycleObserverAlwaysActive(true)
            .autoClear(false)
        registerActivityLifecycleCallbacks(ActivityHelp)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES,
            Configuration.UI_MODE_NIGHT_NO -> applyDayNight()
        }
    }


    fun applyDayNight() {
        AppConfig.upEInkMode()
        ReadBookConfig.upBg()
        ThemeConfig.applyTheme(this)
        initNightMode()
        postEvent(EventBus.RECREATE, "")
    }


    private fun initNightMode() {
        val targetMode =
            if (AppConfig.isNightTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        AppCompatDelegate.setDefaultNightMode(targetMode)
    }

    /**
     * 创建通知ID
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannelId() {
        (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.let {
            //用唯一的ID创建渠道对象
            val downloadChannel = NotificationChannel(
                channelIdDownload,
                getString(R.string.action_download),
                NotificationManager.IMPORTANCE_LOW
            )
            //初始化channel
            downloadChannel.enableLights(false)
            downloadChannel.enableVibration(false)
            downloadChannel.setSound(null, null)

            //用唯一的ID创建渠道对象
            val readAloudChannel = NotificationChannel(
                channelIdReadAloud,
                getString(R.string.read_aloud),
                NotificationManager.IMPORTANCE_LOW
            )
            //初始化channel
            readAloudChannel.enableLights(false)
            readAloudChannel.enableVibration(false)
            readAloudChannel.setSound(null, null)

            //向notification manager 提交channel
            it.createNotificationChannels(listOf(downloadChannel, readAloudChannel))
        }
    }


    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory()
        }
        Glide.get(this).trimMemory(level)
    }

}