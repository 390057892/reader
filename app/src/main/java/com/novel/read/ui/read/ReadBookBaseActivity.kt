package com.novel.read.ui.read

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.PreferKey
import com.novel.read.data.db.entity.Bookmark
import com.novel.read.databinding.ActivityReadBookBinding
import com.novel.read.databinding.DialogDownloadChoiceBinding
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

/**
 * 阅读界面
 */
abstract class ReadBookBaseActivity :
    VMBaseActivity<ActivityReadBookBinding, ReadBookViewModel>() {

    override val viewModel: ReadBookViewModel by viewModels()
    var bottomDialog = 0

    override fun getViewBinding(): ActivityReadBookBinding {
        return ActivityReadBookBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ReadBook.msg = null
        setOrientation()
        upLayoutInDisplayCutoutMode()
        super.onCreate(savedInstanceState)
    }

    /**
     * 屏幕方向
     */
    @SuppressLint("SourceLockedOrientationActivity")
    fun setOrientation() {
        when (AppConfig.requestedDirection) {
            "0" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            "1" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            "2" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            "3" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }

    override fun upNavigationBarColor() {
        when {
            binding.readMenu.isVisible -> super.upNavigationBarColor()
            bottomDialog > 0 -> super.upNavigationBarColor()

            ReadBookConfig.bg is ColorDrawable -> {
                ATH.setNavigationBarColorAuto(this, ReadBookConfig.bgMeanColor)
            }
            else -> {
                ATH.setNavigationBarColorAuto(this, Color.BLACK)
            }
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
    private fun upLayoutInDisplayCutoutMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && AppConfig.readBodyToLh) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
    }

    @SuppressLint("InflateParams")
    fun showDownloadDialog() {
        ReadBook.book?.let { book ->
            alert(titleResource = R.string.offline_cache) {
                val alertBinding = DialogDownloadChoiceBinding.inflate(layoutInflater).apply {
                    root.setBackgroundColor(root.context.backgroundColor)
                    editStart.setText((book.durChapterIndex + 1).toString())
                    editEnd.setText(book.totalChapterNum.toString())
                }
                customView { alertBinding.root }
                yesButton {
                    alertBinding.run {
                        val start = editStart.text?.toString()?.toInt() ?: 0
                        val end = editEnd.text?.toString()?.toInt() ?: book.totalChapterNum
                        CacheBook.start(this@ReadBookBaseActivity, book.bookId, start - 1, end - 1)
                    }
                }
                noButton()
            }.show()
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
                    editText = findViewById<EditText>(R.id.edit_view).apply {
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


    fun showPageAnimConfig(success: () -> Unit) {
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