package com.novel.read.ui.read

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.novel.read.BuildConfig
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.EventBus
import com.novel.read.constant.PreferKey
import com.novel.read.constant.Status
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.help.ReadBookConfig
import com.novel.read.lib.ATH
import com.novel.read.lib.dialogs.alert
import com.novel.read.lib.dialogs.noButton
import com.novel.read.lib.dialogs.okButton
import com.novel.read.service.BaseReadAloudService
import com.novel.read.service.help.ReadAloud
import com.novel.read.service.help.ReadBook
import com.novel.read.ui.chapter.ChapterListActivity
import com.novel.read.ui.info.BookInfoActivity
import com.novel.read.ui.read.config.ReadAdjustDialog
import com.novel.read.ui.read.config.ReadAloudDialog
import com.novel.read.ui.read.config.ReadStyleDialog
import com.novel.read.ui.read.page.ContentTextView
import com.novel.read.ui.read.page.PageView
import com.novel.read.ui.read.page.TextPageFactory
import com.novel.read.ui.read.page.delegate.PageDelegate
import com.novel.read.ui.widget.dialog.AutoReadDialog
import com.novel.read.ui.widget.dialog.TextDialog
import com.novel.read.user.VipHelper
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.activity_read_book.*
import kotlinx.android.synthetic.main.view_read_menu.*
import kotlinx.coroutines.*
import org.jetbrains.anko.sdk27.listeners.onClick
import org.jetbrains.anko.startActivityForResult
import java.lang.Runnable

class ReadBookActivity : VMBaseActivity<ReadBookViewModel>(R.layout.activity_read_book),
    View.OnTouchListener, PageView.CallBack, TextActionMenu.CallBack, ContentTextView.CallBack,
    ReadBook.CallBack,
    ReadMenu.CallBack,
    ReadAloudDialog.CallBack {

    override val viewModel: ReadBookViewModel
        get() = getViewModel(ReadBookViewModel::class.java)

    override val selectedText: String
        get() = page_view.curPage.selectedText

    private val requestCodeChapterList = 568
    private val requestCodeSearchResult = 123
    private var menu: Menu? = null
    private var textActionMenu: TextActionMenu? = null

    override val scope: CoroutineScope get() = this
    override val isInitFinish: Boolean get() = viewModel.isInitFinish
    override val isScroll: Boolean get() = page_view.isScroll
    private val mHandler = Handler(Looper.getMainLooper())
    private val keepScreenRunnable: Runnable =
        Runnable { ReadBookActivityHelp.keepScreenOn(window, false) }
    override var autoPageProgress = 0

    override var isAutoPage = false
    private var screenTimeOut: Long = 0
    private var loadStates: Boolean = false
    override val pageFactory: TextPageFactory get() = page_view.pageFactory
    override val headerHeight: Int get() = page_view.curPage.headerHeight

    override fun onCreate(savedInstanceState: Bundle?) {
        ReadBook.msg = null
        ReadBookActivityHelp.setOrientation(this)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        ReadBookActivityHelp.upLayoutInDisplayCutoutMode(window)
        initView()
        upScreenTimeOut()
        setScreenBrightness(getPrefInt("brightness", 100))
        ReadBook.callBack = this
        ReadBook.titleDate.observe(this) {
            title_bar.title = it
            upMenu()
            upView()
        }
        viewModel.initData(intent)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        upSystemUiVisibility()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        page_view.upStatusBar()
        ReadBook.loadContent(resetPageOffset = false)
    }

    override fun onCompatCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.read_book, menu)
        return super.onCompatCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        upMenu()
        return super.onPrepareOptionsMenu(menu)
    }

    private fun upMenu() {
        menu?.let { menu ->
            ReadBook.book?.let { book ->

            }
        }
    }

    /**
     * 菜单
     */
    override fun onCompatOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                ReadBook.book?.let {
                    ReadBook.curTextChapter = null
                    page_view.upContent()
                    viewModel.refreshContent(it)
                }
            }
            R.id.menu_download -> ReadBookActivityHelp.showDownloadDialog(this)
            R.id.menu_add_bookmark -> ReadBookActivityHelp.showBookMark(this)
            R.id.menu_copy_text ->
                TextDialog.show(supportFragmentManager, ReadBook.curTextChapter?.getContent())
            R.id.menu_update_toc -> ReadBook.book?.let {
                loadChapterList(it)
            }
            R.id.menu_page_anim -> ReadBookActivityHelp.showPageAnimConfig(this) {
                page_view.upPageAnim()
            }
            R.id.menu_book_info -> ReadBook.book?.let {
                BookInfoActivity.actionBookInfo(this, it.bookId, it.bookTypeId)
            }
        }
        return super.onCompatOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        ReadBook.readStartTime = System.currentTimeMillis()
        upSystemUiVisibility()
        page_view.upTime()
    }

    override fun onPause() {
        super.onPause()
        ReadBook.saveRead()
        upSystemUiVisibility()
    }

    override fun upNavigationBarColor() {
        when {
            read_menu == null -> return
            read_menu.isVisible -> {
                ATH.setNavigationBarColorAuto(this)
            }
            ReadBookConfig.bg is ColorDrawable -> {
                ATH.setNavigationBarColorAuto(this, ReadBookConfig.bgMeanColor)
            }
            else -> {
                ATH.setNavigationBarColorAuto(this, Color.BLACK)
            }
        }
    }


    /**
     * 初始化View
     */
    private fun initView() {
        cursor_left.setColorFilter(accentColor)
        cursor_right.setColorFilter(accentColor)
        cursor_left.setOnTouchListener(this)
        cursor_right.setOnTouchListener(this)
        tv_chapter_name.onClick {

        }
        tv_chapter_url.onClick {
            runCatching {
                val url = tv_chapter_url.text.toString()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        }

        adView.setBackgroundColor(ReadBookConfig.bgMeanColor)
        adView.alpha = 0.3f
        if (VipHelper.showAd()) {
//            adView.visibility = View.VISIBLE
//            //底部banner google
//            val mPublisherAdView = PublisherAdView(this)
//            mPublisherAdView.adUnitId = "ca-app-pub-5528897088703176/7116750145"
//            adView.removeAllViews()
//            adView.addView(mPublisherAdView)
//            val adRequest = PublisherAdRequest.Builder().build()
//            mPublisherAdView.setAdSizes(getAdSize(), AdSize.BANNER)
//            mPublisherAdView.loadAd(adRequest)
        } else {
            adView.visibility = View.GONE
        }


    }

//    private fun getAdSize(): AdSize? {
//        val display = windowManager.defaultDisplay
//        val outMetrics = DisplayMetrics()
//        display.getMetrics(outMetrics)
//        val widthPixels = outMetrics.widthPixels.toFloat()
//        val density = outMetrics.density
//        val adWidth = (widthPixels / density).toInt()
//        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
//    }

    override fun clickCenter() {
        when {
            BaseReadAloudService.isRun -> {
                showReadAloudDialog()
            }
            isAutoPage -> {
                AutoReadDialog().show(supportFragmentManager, "autoRead")
            }
            else -> {
                read_menu.runMenuIn()
            }
        }
    }


    override fun showTextActionMenu() {
        textActionMenu ?: let {
            textActionMenu = TextActionMenu(this, this)
        }
        textActionMenu?.let { popup ->
            popup.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val popupHeight = popup.contentView.measuredHeight
            val x = text_menu_position.x.toInt()
            var y = text_menu_position.y.toInt() - popupHeight
            if (y < statusBarHeight) {
                y = (cursor_left.y + cursor_left.height).toInt()
            }
            if (cursor_right.y > y && cursor_right.y < y + popupHeight) {
                y = (cursor_right.y + cursor_right.height).toInt()
            }
            if (!popup.isShowing) {
                popup.showAtLocation(text_menu_position, Gravity.TOP or Gravity.START, x, y)
            } else {
                popup.update(
                    x, y,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    }

    override fun loadChapterList(book: Book) {
        ReadBook.upMsg(getString(R.string.toc_updateing))
        viewModel.loadChapterList(book)
    }

    override fun upContent(relativePosition: Int, resetPageOffset: Boolean) {
        autoPageProgress = 0
        launch {
            page_view.upContent(relativePosition, resetPageOffset)
            seek_read_page.progress = ReadBook.durPageIndex
        }
        loadStates = false
    }

    override fun upView() {
        launch {
            ReadBook.curTextChapter?.let {
                tv_chapter_name.text = it.title
//                tv_chapter_name.visible()
                if (!ReadBook.isLocalBook) {
                    tv_chapter_url.text = it.title
//                    tv_chapter_url.visible()
                } else {
                    tv_chapter_url.gone()
                }
                seek_read_page.max = it.pageSize.minus(1)
                seek_read_page.progress = ReadBook.durPageIndex
                tv_pre.isEnabled = ReadBook.durChapterIndex != 0
                tv_next.isEnabled = ReadBook.durChapterIndex != ReadBook.chapterSize - 1
            } ?: let {
                tv_chapter_name.gone()
                tv_chapter_url.gone()
            }
        }
    }

    override fun pageChanged() {
        autoPageProgress = 0
        launch {
            seek_read_page.progress = ReadBook.durPageIndex
        }
    }

    override fun contentLoadFinish() {
        if (intent.getBooleanExtra("readAloud", false)) {
            intent.removeExtra("readAloud")
            ReadBook.readAloud()
        }
        loadStates = true
    }

    override fun upSelectedStart(x: Float, y: Float, top: Float) {
        cursor_left.x = x - cursor_left.width
        cursor_left.y = y
        cursor_left.visible(true)
        text_menu_position.x = x
        text_menu_position.y = top
    }

    override fun upSelectedEnd(x: Float, y: Float) {
        cursor_right.x = x
        cursor_right.y = y
        cursor_right.visible(true)
    }

    override fun onCancelSelect() {
        cursor_left.invisible()
        cursor_right.invisible()
        textActionMenu?.dismiss()
    }

    private fun upScreenTimeOut() {
        getPrefString(PreferKey.keepLight)?.let {
            screenTimeOut = it.toLong() * 1000
        }
        screenOffTimerStart()
    }

    override fun screenOffTimerStart() {
        if (screenTimeOut < 0) {
            ReadBookActivityHelp.keepScreenOn(window, true)
            return
        }
        val t = screenTimeOut - sysScreenOffTime
        if (t > 0) {
            mHandler.removeCallbacks(keepScreenRunnable)
            ReadBookActivityHelp.keepScreenOn(window, true)
            mHandler.postDelayed(keepScreenRunnable, screenTimeOut)
        } else {
            ReadBookActivityHelp.keepScreenOn(window, false)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> textActionMenu?.dismiss()
            MotionEvent.ACTION_MOVE -> {
                when (v.id) {
                    R.id.cursor_left -> page_view.curPage.selectStartMove(
                        event.rawX + cursor_left.width,
                        event.rawY - cursor_left.height
                    )
                    R.id.cursor_right -> page_view.curPage.selectEndMove(
                        event.rawX - cursor_right.width,
                        event.rawY - cursor_right.height
                    )
                }
            }
            MotionEvent.ACTION_UP -> showTextActionMenu()
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when {
            ReadBookActivityHelp.isPrevKey(this, keyCode) -> {
                if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
                    page_view.pageDelegate?.keyTurnPage(PageDelegate.Direction.PREV)
                    return true
                }
            }
            ReadBookActivityHelp.isNextKey(this, keyCode) -> {
                if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
                    page_view.pageDelegate?.keyTurnPage(PageDelegate.Direction.NEXT)
                    return true
                }
            }
            keyCode == KeyEvent.KEYCODE_VOLUME_UP -> {
                if (volumeKeyPage(PageDelegate.Direction.PREV)) {
                    return true
                }
            }
            keyCode == KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (volumeKeyPage(PageDelegate.Direction.NEXT)) {
                    return true
                }
            }
            keyCode == KeyEvent.KEYCODE_SPACE -> {
                page_view.pageDelegate?.keyTurnPage(PageDelegate.Direction.NEXT)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 音量键翻页
     */
    private fun volumeKeyPage(direction: PageDelegate.Direction): Boolean {
        if (!read_menu.isVisible) {
            if (getPrefBoolean("volumeKeyPage", true)) {
                if (getPrefBoolean("volumeKeyPageOnPlay")
                    || BaseReadAloudService.pause
                ) {
                    page_view.pageDelegate?.isCancel = false
                    page_view.pageDelegate?.keyTurnPage(direction)
                    return true
                }
            }
        }
        return false
    }

    override fun observeLiveBus() {
        super.observeLiveBus()
        observeEvent<String>(EventBus.TIME_CHANGED) { page_view.upTime() }
        observeEvent<Int>(EventBus.BATTERY_CHANGED) { page_view.upBattery(it) }
        observeEvent<BookChapter>(EventBus.OPEN_CHAPTER) {
            viewModel.openChapter(it.chapterIndex, ReadBook.durPageIndex)
            page_view.upContent()
        }
        observeEvent<Boolean>(EventBus.MEDIA_BUTTON) {
            if (it) {
                onClickReadAloud()
            } else {
                ReadBook.readAloud(!BaseReadAloudService.pause)
            }
        }
        observeEvent<Boolean>(EventBus.UP_CONFIG) {
            upSystemUiVisibility()
            page_view.upBg()
            page_view.upTipStyle()
            page_view.upStyle()
            if (it) {
                ReadBook.loadContent(resetPageOffset = false)
            } else {
                page_view.upContent(resetPageOffset = false)
            }
            adView.setBackgroundColor(ReadBookConfig.bgMeanColor)
        }
        observeEvent<Int>(EventBus.ALOUD_STATE) {
            if (it == Status.STOP || it == Status.PAUSE) {
                ReadBook.curTextChapter?.let { textChapter ->
                    val page = textChapter.page(ReadBook.durPageIndex)
                    if (page != null) {
                        page.removePageAloudSpan()
                        page_view.upContent(resetPageOffset = false)
                    }
                }
            }
        }
        observeEventSticky<Int>(EventBus.TTS_PROGRESS) { chapterStart ->
            launch(Dispatchers.IO) {
                if (BaseReadAloudService.isPlay()) {
                    ReadBook.curTextChapter?.let { textChapter ->
                        val pageStart =
                            chapterStart - textChapter.getReadLength(ReadBook.durPageIndex)
                        textChapter.page(ReadBook.durPageIndex)?.upPageAloudSpan(pageStart)
                        upContent()
                    }
                }
            }
        }
        observeEvent<Boolean>(PreferKey.keepLight) {
            upScreenTimeOut()
        }
        observeEvent<Boolean>(PreferKey.textSelectAble) {
            page_view.curPage.upSelectAble(it)
        }
        observeEvent<String>(PreferKey.showBrightnessView) {
            setScreenBrightness(getPrefInt("brightness", 100))
        }

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
        val params = window?.attributes
        params?.screenBrightness = brightness
        window?.attributes = params
    }

    private fun brightnessAuto(): Boolean {
        return getPrefBoolean("brightnessAuto", true)
    }

    override fun autoPage() {
    }

    override fun openReplaceRule() {
    }

    override fun showMenuBar() {
        read_menu.runMenuIn()
    }

    override fun openChapterList() {
        ReadBook.book?.let {
            startActivityForResult<ChapterListActivity>(
                requestCodeChapterList,
                Pair("bookId", it.bookId.toString())
            )
        }
    }

    override fun openSearchActivity(searchWord: String?) {

    }

    override fun showReadStyle() {
        ReadStyleDialog().show(supportFragmentManager, "readStyle")
    }

    override fun showAdjust() {
        ReadAdjustDialog().show(supportFragmentManager, "readStyle")
    }

    override fun showReadAloudDialog() {
        ReadAloudDialog().show(supportFragmentManager, "readAloud")
    }

    override fun upSystemUiVisibility() {
    }

    override fun onClickReadAloud() {
        when {
            !BaseReadAloudService.isRun -> ReadBook.readAloud()
            BaseReadAloudService.pause -> ReadAloud.resume(this)
            else -> ReadAloud.pause(this)
        }
    }

    fun showBgTextConfig() {
//        BgTextConfigDialog().show(supportFragmentManager, "bgTextConfig")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestCodeChapterList ->
                    data?.getIntExtra("index", ReadBook.durChapterIndex)?.let { index ->
                        if (index != ReadBook.durChapterIndex) {
                            val pageIndex = data.getIntExtra("pageIndex", 0)
                            viewModel.openChapter(index, pageIndex)
                        }
                    }
                requestCodeSearchResult ->
                    data?.getIntExtra("index", ReadBook.durChapterIndex)?.let { index ->
                        launch(Dispatchers.IO) {
                            val indexWithinChapter = data.getIntExtra("indexWithinChapter", 0)
                            viewModel.searchContentQuery = data.getStringExtra("query") ?: ""
                            viewModel.openChapter(index)
                            // block until load correct chapter and pages
                            var pages = ReadBook.curTextChapter?.pages
                            while (ReadBook.durChapterIndex != index || pages == null) {
                                delay(100L)
                                pages = ReadBook.curTextChapter?.pages
                            }
                            val positions =
                                ReadBook.searchResultPositions(
                                    pages,
                                    indexWithinChapter,
                                    viewModel.searchContentQuery
                                )
                            while (ReadBook.durPageIndex != positions[0]) {
                                delay(100L)
                                ReadBook.skipToPage(positions[0])
                            }
                            withContext(Dispatchers.Main) {
                                page_view.curPage.selectStartMoveIndex(
                                    0,
                                    positions[1],
                                    positions[2]
                                )
                                delay(20L)
                                when (positions[3]) {
                                    0 -> page_view.curPage.selectEndMoveIndex(
                                        0,
                                        positions[1],
                                        positions[2] + viewModel.searchContentQuery.length - 1
                                    )
                                    1 -> page_view.curPage.selectEndMoveIndex(
                                        0,
                                        positions[1] + 1,
                                        positions[4]
                                    )
                                    //todo: consider change page, jump to scroll position
                                    -1 -> page_view.curPage.selectEndMoveIndex(1, 0, positions[4])
                                }
                                page_view.isTextSelected = true
                                delay(100L)
                            }
                        }
                    }
            }

        }
    }

    override fun onMenuItemSelected(itemId: Int): Boolean {
        when (itemId) {
            R.id.menu_search_content -> {
                viewModel.searchContentQuery = selectedText
                openSearchActivity(selectedText)
                return true
            }
        }
        return false
    }

    override fun onMenuActionFinally() {
        textActionMenu?.dismiss()
        page_view.curPage.cancelSelect()
        page_view.isTextSelected = false
    }

    override fun finish() {
        ReadBook.book?.let {
            if (!ReadBook.inBookshelf) {
                this.alert(title = getString(R.string.add_to_shelf)) {
                    message = getString(R.string.check_add_bookshelf, it.bookName)
                    okButton {
                        ReadBook.inBookshelf = true
                        setResult(Activity.RESULT_OK)
                        super.finish()
                    }
                    noButton { viewModel.removeFromBookshelf { super.finish() } }
                }.show().applyTint()
            } else {
                super.finish()
            }
        } ?: super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        postEvent(EventBus.UP_BOOK, 0L)
        mHandler.removeCallbacks(keepScreenRunnable)
        textActionMenu?.dismiss()
        page_view.onDestroy()
        ReadBook.msg = null
        if (!BuildConfig.DEBUG) {
//            SyncBookProgress.uploadBookProgress()
//            Backup.autoBack(this)
        }
    }

}