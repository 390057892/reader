package com.novel.read.ui.read

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.AsyncTask
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import com.novel.read.App
import com.novel.read.R
import com.novel.read.constant.PreferKey
import com.novel.read.data.db.entity.Bookmark
import com.novel.read.help.AppConfig
import com.novel.read.help.ReadBookConfig
import com.novel.read.lib.ATH
import com.novel.read.lib.ThemeStore
import com.novel.read.lib.dialogs.*
import com.novel.read.service.help.CacheBook
import com.novel.read.service.help.ReadBook
import com.novel.read.utils.ext.applyTint
import com.novel.read.utils.ext.backgroundColor
import com.novel.read.utils.ext.getPrefString
import com.novel.read.utils.ext.requestInputMethod
import kotlinx.android.synthetic.main.dialog_download_choice.view.*
import kotlinx.android.synthetic.main.dialog_edit_text.view.*
import org.jetbrains.anko.layoutInflater


object ReadBookActivityHelp {

    /**
     * 更新状态栏,导航栏
     */
    fun upSystemUiVisibility(
        window: Window,
        isInMultiWindow: Boolean,
        toolBarHide: Boolean = true
    ) {
        var flag = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (!isInMultiWindow) {
            flag = flag or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (ReadBookConfig.hideNavigationBar) {
            flag = flag or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        if (toolBarHide) {
            if (ReadBookConfig.hideStatusBar) {
                flag = flag or View.SYSTEM_UI_FLAG_FULLSCREEN
            }
            if (ReadBookConfig.hideNavigationBar) {
                flag = flag or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
        }
        window.decorView.systemUiVisibility = flag
        if (toolBarHide) {
            ATH.setLightStatusBar(window, ReadBookConfig.durConfig.curStatusIconDark())
        } else {
            ATH.setLightStatusBarAuto(
                window,
                ThemeStore.statusBarColor(App.INSTANCE, AppConfig.isTransparentStatusBar)
            )
        }
    }

    /**
     * 屏幕方向
     */
    @SuppressLint("SourceLockedOrientationActivity")
    fun setOrientation(activity: Activity) = activity.apply {
        when (AppConfig.requestedDirection) {
            "0" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            "1" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            "2" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            "3" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }


    /**
     * 保持亮屏
     */
    fun keepScreenOn(window: Window, on: Boolean) {
        if (on) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    /**
     * 适配刘海
     */
    fun upLayoutInDisplayCutoutMode(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && AppConfig.readBodyToLh) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
    }

    @SuppressLint("InflateParams")
    fun showDownloadDialog(context: Context) {
        ReadBook.book?.let { book ->
            context.alert(titleResource = R.string.offline_cache) {
                var view: View? = null
                customView {
                    LayoutInflater.from(context).inflate(R.layout.dialog_download_choice, null)
                        .apply {
                            view = this
                            setBackgroundColor(context.backgroundColor)
                            edit_start.setText((book.durChapterIndex + 1).toString())
                            edit_end.setText(
                                App.db.getChapterDao().getChapterCount(bookId = book.bookId).toString()
                            )
                        }
                }
                yesButton {
                    view?.apply {
                        val start = edit_start?.text?.toString()?.toInt() ?: 0
                        val end = edit_end?.text?.toString()?.toInt() ?: App.db.getChapterDao()
                            .getChapterCount(bookId = book.bookId)
                        CacheBook.start(context, book.bookId, start - 1, end - 1)
                    }
                }
                noButton()
            }.show().applyTint()
        }
    }

    @SuppressLint("InflateParams")
    fun showBookMark(context: Context) = with(context) {
        val book = ReadBook.book ?: return
        val textChapter = ReadBook.curTextChapter ?: return
        context.alert(title = getString(R.string.bookmark_add)) {
            var editText: EditText? = null
            message = book.bookName + " • " + textChapter.title
            customView {
                layoutInflater.inflate(R.layout.dialog_edit_text, null).apply {
                    editText = edit_view.apply {
                        setHint(R.string.note_content)
                    }
                }
            }
            yesButton {
                editText?.text?.toString()?.let { editContent ->
                    AsyncTask.execute {
                        val bookmark = Bookmark(
                            bookId = book.bookId,
                            bookName = book.bookName,
                            chapterIndex = ReadBook.durChapterIndex,
                            pageIndex = ReadBook.durPageIndex,
                            chapterName = textChapter.title,
                            content = editContent
                        )
                        App.db.getBookMarkDao().insert(bookmark)
                    }
                }
            }
            noButton()
        }.show().applyTint().requestInputMethod()
    }

    @SuppressLint("InflateParams")
    fun showCharsetConfig(context: Context) = with(context) {
//        val charsets =
//            arrayListOf("UTF-8", "GB2312", "GBK", "Unicode", "UTF-16", "UTF-16LE", "ASCII")
//        alert(R.string.set_charset) {
//            var editText: AutoCompleteTextView? = null
//            customView {
//                layoutInflater.inflate(R.layout.dialog_edit_text, null).apply {
//                    editText = edit_view
//                    edit_view.setFilterValues(charsets)
//                    edit_view.setText(ReadBook.book?.charset)
//                }
//            }
//            okButton {
//                val text = editText?.text?.toString()
//                text?.let {
//                    ReadBook.setCharset(it)
//                }
//            }
//            cancelButton()
//        }.show().applyTint()
    }

    fun showPageAnimConfig(context: Context, success: () -> Unit) = with(context) {
        val items = arrayListOf<String>()
        items.add(getString(R.string.btn_default_s))
        items.add(getString(R.string.page_anim_cover))
        items.add(getString(R.string.page_anim_slide))
        items.add(getString(R.string.page_anim_simulation))
        items.add(getString(R.string.page_anim_scroll))
        items.add(getString(R.string.page_anim_none))
        selector(R.string.page_anim, items) { _, i ->
            ReadBookConfig.pageAnim
            success()
        }
    }

    fun isPrevKey(context: Context, keyCode: Int): Boolean {
        val prevKeysStr = context.getPrefString(PreferKey.prevKeys)
        return prevKeysStr?.split(",")?.contains(keyCode.toString()) ?: false
    }

    fun isNextKey(context: Context, keyCode: Int): Boolean {
        val nextKeysStr = context.getPrefString(PreferKey.nextKeys)
        return nextKeysStr?.split(",")?.contains(keyCode.toString()) ?: false
    }
}