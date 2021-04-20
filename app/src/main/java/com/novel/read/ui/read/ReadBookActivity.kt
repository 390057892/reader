package com.novel.read.ui.read

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.novel.read.BuildConfig
import com.novel.read.R
import com.novel.read.constant.EventBus
import com.novel.read.constant.PreferKey
import com.novel.read.constant.Status
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
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
import com.novel.read.utils.ext.*
import kotlinx.coroutines.*
import org.jetbrains.anko.startActivityForResult
import java.lang.Runnable

class ReadBookActivity :ReadBookBaseActivity(),
    View.OnTouchListener, PageView.CallBack, TextActionMenu.CallBack, ContentTextView.CallBack,
    ReadBook.CallBack,
    ReadMenu.CallBack,
    ReadAloudDialog.CallBack {

    override val selectedText: String
        get() = binding.pageView.curPage.selectedText

    private val requestCodeChapterList = 568
    private val requestCodeSearchResult = 123
    private var menu: Menu? = null
    private val textActionMenu: TextActionMenu by lazy {
        TextActionMenu(this, this)
    }

    override val scope: CoroutineScope get() = this
    override val isInitFinish: Boolean get() = viewModel.isInitFinish
    override val isScroll: Boolean get() = binding.pageView.isScroll
    private val mHandler = Handler(Looper.getMainLooper())
    private val keepScreenRunnable: Runnable =
        Runnable { keepScreenOn(window, false) }
    override var autoPageProgress = 0

    override var isAutoPage = false
    private var screenTimeOut: Long = 0
    private var loadStates: Boolean = false
    override val pageFactory: TextPageFactory get() = binding.pageView.pageFactory
    override val headerHeight: Int get() = binding.pageView.curPage.headerHeight

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initView()
        upScreenTimeOut()
        setScreenBrightness(getPrefInt("brightness", 100))
        ReadBook.callBack = this
        ReadBook.titleDate.observe(this) {
            binding.readMenu.setTitle(it)
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
        binding.pageView.upStatusBar()
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
                    binding.pageView.upContent()
                    viewModel.refreshContent(it)
                }
            }
            R.id.menu_download -> showDownloadDialog()
            R.id.menu_add_bookmark -> showBookMark(this)
            R.id.menu_copy_text ->
                TextDialog.show(supportFragmentManager, ReadBook.curTextChapter?.getContent())
            R.id.menu_update_toc -> ReadBook.book?.let {
                loadChapterList(it)
            }
            R.id.menu_page_anim -> showPageAnimConfig {
                binding.pageView.upPageAnim()
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
        binding.pageView.upTime()
    }

    override fun onPause() {
        super.onPause()
        ReadBook.saveRead()
        upSystemUiVisibility()
    }

    /**
     * 初始化View
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.cursorLeft.setColorFilter(accentColor)
        binding.cursorRight.setColorFilter(accentColor)
        binding.cursorLeft.setOnTouchListener(this)
        binding.cursorRight.setOnTouchListener(this)
    }

    override fun clickCenter() {
        when {
            BaseReadAloudService.isRun -> {
                showReadAloudDialog()
            }
            isAutoPage -> {
                AutoReadDialog().show(supportFragmentManager, "autoRead")
            }
            else -> {
                binding.readMenu.runMenuIn()
            }
        }
    }


    override fun showTextActionMenu() = with(binding){
        textActionMenu.let { popup ->
            popup.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val popupHeight = popup.contentView.measuredHeight
            val x = textMenuPosition.x.toInt()
            var y = textMenuPosition.y.toInt() - popupHeight
            if (y < statusBarHeight) {
                y = (cursorLeft.y + cursorLeft.height).toInt()
            }
            if (cursorRight.y > y && cursorRight.y < y + popupHeight) {
                y = (cursorRight.y + cursorRight.height).toInt()
            }
            if (!popup.isShowing) {
                popup.showAtLocation(textMenuPosition, Gravity.TOP or Gravity.START, x, y)
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
            binding.pageView.upContent(relativePosition, resetPageOffset)
            binding.readMenu.setSeekPage(ReadBook.durPageIndex)
            loadStates = false
        }
        loadStates = false
    }

    override fun upView() {
        launch {
            binding.readMenu.upBookView()
        }
    }

    override fun upPageAnim() {
        launch {
            binding.pageView.upPageAnim()
        }
    }

    override fun pageChanged() {
        autoPageProgress = 0
        launch {
            binding.readMenu.setSeekPage(ReadBook.durPageIndex)
        }
    }

    override fun contentLoadFinish() {
        if (intent.getBooleanExtra("readAloud", false)) {
            intent.removeExtra("readAloud")
            ReadBook.readAloud()
        }
        loadStates = true
    }

    override fun upSelectedStart(x: Float, y: Float, top: Float)  = with(binding){
        cursorLeft.x = x - cursorLeft.width
        cursorLeft.y = y
        cursorLeft.visible(true)
        textMenuPosition.x = x
        textMenuPosition.y = top
    }

    override fun upSelectedEnd(x: Float, y: Float) = with(binding){
        cursorRight.x = x
        cursorRight.y = y
        cursorRight.visible(true)
    }

    /**
     * 取消文字选择
     */
    override fun onCancelSelect() = with(binding) {
        cursorLeft.invisible()
        cursorRight.invisible()
        textActionMenu.dismiss()
    }
    private fun upScreenTimeOut() {
        getPrefString(PreferKey.keepLight)?.let {
            screenTimeOut = it.toLong() * 1000
        }
        screenOffTimerStart()
    }

    override fun screenOffTimerStart() {
        if (screenTimeOut < 0) {
            keepScreenOn(window, true)
            return
        }
        val t = screenTimeOut - sysScreenOffTime
        if (t > 0) {
            mHandler.removeCallbacks(keepScreenRunnable)
            keepScreenOn(window, true)
            mHandler.postDelayed(keepScreenRunnable, screenTimeOut)
        } else {
            keepScreenOn(window, false)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean = with(binding){
        when (event.action) {
            MotionEvent.ACTION_DOWN -> textActionMenu.dismiss()
            MotionEvent.ACTION_MOVE -> {
                when (v.id) {
                    R.id.cursor_left -> binding.pageView.curPage.selectStartMove(
                        event.rawX + cursorLeft.width,
                        event.rawY - cursorLeft.height
                    )
                    R.id.cursor_right -> binding.pageView.curPage.selectEndMove(
                        event.rawX - cursorRight.width,
                        event.rawY - cursorRight.height
                    )
                }
            }
            MotionEvent.ACTION_UP -> showTextActionMenu()
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when {
            isPrevKey(this, keyCode) -> {
                if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
                    binding.pageView.pageDelegate?.keyTurnPage(PageDelegate.Direction.PREV)
                    return true
                }
            }
            isNextKey(this, keyCode) -> {
                if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
                    binding.pageView.pageDelegate?.keyTurnPage(PageDelegate.Direction.NEXT)
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
                binding.pageView.pageDelegate?.keyTurnPage(PageDelegate.Direction.NEXT)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 音量键翻页
     */
    private fun volumeKeyPage(direction: PageDelegate.Direction): Boolean {
        if (!binding.readMenu.isVisible) {
            if (getPrefBoolean("volumeKeyPage", true)) {
                if (getPrefBoolean("volumeKeyPageOnPlay")
                    || BaseReadAloudService.pause
                ) {
                    binding.pageView.pageDelegate?.isCancel = false
                    binding.pageView.pageDelegate?.keyTurnPage(direction)
                    return true
                }
            }
        }
        return false
    }

    override fun observeLiveBus() {
        super.observeLiveBus()
        observeEvent<String>(EventBus.TIME_CHANGED) { binding.pageView.upTime() }
        observeEvent<Int>(EventBus.BATTERY_CHANGED) { binding.pageView.upBattery(it) }
        observeEvent<BookChapter>(EventBus.OPEN_CHAPTER) {
            viewModel.openChapter(it.chapterIndex, ReadBook.durPageIndex)
            binding.pageView.upContent()
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
            binding.pageView.upBg()
            binding.pageView.upTipStyle()
            binding.pageView.upStyle()
            if (it) {
                ReadBook.loadContent(resetPageOffset = false)
            } else {
                binding.pageView.upContent(resetPageOffset = false)
            }
        }
        observeEvent<Int>(EventBus.ALOUD_STATE) {
            if (it == Status.STOP || it == Status.PAUSE) {
                ReadBook.curTextChapter?.let { textChapter ->
                    val page = textChapter.page(ReadBook.durPageIndex)
                    if (page != null) {
                        page.removePageAloudSpan()
                        binding.pageView.upContent(resetPageOffset = false)
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
            binding.pageView.curPage.upSelectAble(it)
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
        binding.readMenu.runMenuIn()
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
                                binding.pageView.curPage.selectStartMoveIndex(
                                    0,
                                    positions[1],
                                    positions[2]
                                )
                                delay(20L)
                                when (positions[3]) {
                                    0 -> binding.pageView.curPage.selectEndMoveIndex(
                                        0,
                                        positions[1],
                                        positions[2] + viewModel.searchContentQuery.length - 1
                                    )
                                    1 -> binding.pageView.curPage.selectEndMoveIndex(
                                        0,
                                        positions[1] + 1,
                                        positions[4]
                                    )
                                    //todo: consider change page, jump to scroll position
                                    -1 -> binding.pageView.curPage.selectEndMoveIndex(1, 0, positions[4])
                                }
                                binding.pageView.isTextSelected = true
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
        textActionMenu.dismiss()
        binding.pageView.curPage.cancelSelect()
        binding.pageView.isTextSelected = false
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
        binding.pageView.onDestroy()
        ReadBook.msg = null
        if (!BuildConfig.DEBUG) {
//            SyncBookProgress.uploadBookProgress()
//            Backup.autoBack(this)
        }
    }

}