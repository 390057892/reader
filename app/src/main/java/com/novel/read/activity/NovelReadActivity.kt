package com.novel.read.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.*
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View.*
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.common_lib.base.utils.ToastUtils
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.adapter.CategoryAdapter
import com.novel.read.adapter.MarkAdapter
import com.novel.read.base.NovelBaseActivity
import com.novel.read.constants.Constant
import com.novel.read.constants.Constant.ResultCode.Companion.RESULT_IS_COLLECTED
import com.novel.read.event.*
import com.novel.read.http.AccountManager
import com.novel.read.model.db.BookChapterBean
import com.novel.read.model.db.CollBookBean
import com.novel.read.model.db.DownloadTaskBean
import com.novel.read.model.db.dbManage.BookRepository
import com.novel.read.model.protocol.MarkResp
import com.novel.read.service.DownloadMessage
import com.novel.read.service.DownloadService
import com.novel.read.utlis.BrightnessUtils
import com.novel.read.utlis.ScreenUtils
import com.novel.read.utlis.SpUtil
import com.novel.read.utlis.SystemBarUtils
import com.novel.read.widget.dialog.ReadSettingDialog
import com.novel.read.widget.page.PageLoader
import com.novel.read.widget.page.PageView
import com.novel.read.widget.page.ReadSettingManager
import com.novel.read.widget.page.TxtChapter
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_read.*
import kotlinx.android.synthetic.main.layout_download.*
import kotlinx.android.synthetic.main.layout_light.*
import kotlinx.android.synthetic.main.layout_read_mark.*
import java.util.*

class NovelReadActivity : NovelBaseActivity(), DownloadService.OnDownloadListener {

    private var mCategoryAdapter: CategoryAdapter? = null
    private val mChapters = ArrayList<TxtChapter>()
    private var mCurrentChapter: TxtChapter? = null //当前章节
    private var currentChapter = 0
    private var mMarkAdapter: MarkAdapter? = null
    private val mMarks = ArrayList<MarkResp.SignBean>()
    private lateinit var mPageLoader: PageLoader
    private var mTopInAnim: Animation? = null
    private var mTopOutAnim: Animation? = null
    private var mBottomInAnim: Animation? = null
    private var mBottomOutAnim: Animation? = null

    private var mSettingDialog: ReadSettingDialog? = null
    private var isCollected = false // isFromSDCard
    private var isNightMode = false
    private var isFullScreen = false
    private val isRegistered = false

    private var mCollBook: CollBookBean? = null
    private var mBookId: String = ""

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                WHAT_CATEGORY -> rlv_list.setSelection(mPageLoader.chapterPos)
                WHAT_CHAPTER -> mPageLoader.openChapter()
            }
        }
    }

    override val layoutId: Int get() = R.layout.activity_read

    // 接收电池信息和时间更新的广播
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Objects.requireNonNull(intent.action) == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra("level", 0)
                mPageLoader.updateBattery(level)
            } else if (intent.action == Intent.ACTION_TIME_TICK) {
                mPageLoader.updateTime()
            }// 监听分钟的变化
        }
    }

    private var mService: DownloadService.IDownloadManager? = null
    private var mConn: ServiceConnection? = null

    override fun initView() {
        EventManager.instance.registerSubscriber(this)
        mCollBook = intent.getSerializableExtra(EXTRA_COLL_BOOK) as CollBookBean
        isCollected = intent.getBooleanExtra(EXTRA_IS_COLLECTED, false)
        mBookId = mCollBook!!.id
        initService()
        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            read_pv_page.setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //获取页面加载器
        mPageLoader = read_pv_page.getPageLoader(mCollBook)

        mSettingDialog = ReadSettingDialog(this, mPageLoader)
        //禁止滑动展示DrawerLayout
        read_dl_slide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //侧边打开后，返回键能够起作用
        read_dl_slide.isFocusableInTouchMode = false
        //半透明化StatusBar
        SystemBarUtils.transparentStatusBar(this)
        //隐藏StatusBar
        read_pv_page!!.post { this.hideSystemBar() }
        read_abl_top_menu.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0)
        ll_download.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, ScreenUtils.dpToPx(15))

        val lp = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = 1
        }
        window.attributes = lp

        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto) {
            BrightnessUtils.setDefaultBrightness(this)
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().brightness)
        }

        //注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(mReceiver, intentFilter)

        if (!SpUtil.getBooleanValue(Constant.BookGuide, false)) {
            iv_guide.visibility = VISIBLE
            toggleMenu(false)
        }

        Log.e(TAG, "mBookId: $mBookId")
        if (isCollected) {
            mPageLoader.collBook.bookChapters =
                BookRepository.getInstance().getBookChaptersInRx(mBookId)
            // 刷新章节列表
            mPageLoader.refreshChapterList()
            // 如果是网络小说并被标记更新的，则从网络下载目录
            if (mCollBook!!.isUpdate && !mCollBook!!.isLocal) {
                AccountManager.getInstance().getBookArticle(mBookId, "2", "1", "10000")
            }
        } else {
            AccountManager.getInstance().getBookArticle(mBookId, "2", "1", "10000")
        }
    }

    override fun initData() {
        tv_book_name.text = mCollBook!!.title
        mCategoryAdapter = CategoryAdapter()
        rlv_list.adapter = mCategoryAdapter
        rlv_list.isFastScrollEnabled = true
        rlv_mark.layoutManager = LinearLayoutManager(this)
        mMarkAdapter = MarkAdapter(mMarks)
        rlv_mark.adapter = mMarkAdapter
        isNightMode = ReadSettingManager.getInstance().isNightMode
        //夜间模式按钮的状态
        toggleNightMode()
        isFullScreen = ReadSettingManager.getInstance().isFullScreen
        toolbar.setNavigationOnClickListener { finish() }
        read_setting_sb_brightness.progress = ReadSettingManager.getInstance().brightness
        mPageLoader.setOnPageChangeListener(
            object : PageLoader.OnPageChangeListener {

                override fun onChapterChange(pos: Int) {
                    mCategoryAdapter!!.setChapter(pos)
                    mCurrentChapter = mChapters[pos]
                    currentChapter = pos
                }

                override fun requestChapters(requestChapters: List<TxtChapter>) {
                    AccountManager.getInstance().getBookArticleDetail(mBookId, requestChapters)
                    mHandler.sendEmptyMessage(WHAT_CATEGORY)
                }

                override fun onCategoryFinish(chapters: List<TxtChapter>) {
                    mChapters.clear()
                    mChapters.addAll(chapters)
                    mCategoryAdapter!!.refreshItems(mChapters)
                }

                override fun onPageCountChange(count: Int) {}

                override fun onPageChange(pos: Int) {

                }
            }
        )
        read_pv_page.setTouchListener(object : PageView.TouchListener {
            override fun onTouch(): Boolean {
                return !hideReadMenu()
            }

            override fun center() {
                toggleMenu(true)
            }

            override fun prePage() {}

            override fun nextPage() {}

            override fun cancel() {}
        })
        read_tv_category.setOnClickListener {
            //移动到指定位置
            if (mCategoryAdapter!!.count > 0) {
                rlv_list.setSelection(mPageLoader.chapterPos)
            }
            //切换菜单
            toggleMenu(true)
            //打开侧滑动栏
            read_dl_slide.openDrawer(GravityCompat.START)
        }
        tv_light.setOnClickListener {
            ll_light.visibility = GONE
            rlReadMark.visibility = GONE
            if (isVisible(ll_light)) {
                ll_light.visibility = GONE
            } else {
                ll_light.visibility = VISIBLE
            }
        }
        tv_setting.setOnClickListener {
            ll_light.visibility = GONE
            rlReadMark.visibility = GONE
            toggleMenu(false)
            mSettingDialog!!.show()
        }

        read_setting_sb_brightness.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                //设置当前 Activity 的亮度
                BrightnessUtils.setBrightness(this@NovelReadActivity, progress)
                //存储亮度的进度条
                ReadSettingManager.getInstance().brightness = progress
            }
        })

        tvBookReadMode.setOnClickListener {
            isNightMode = !isNightMode
            mPageLoader.setNightMode(isNightMode)
            toggleNightMode()
        }

        read_tv_brief.setOnClickListener {
            val intent = Intent(this, NovelBookDetailActivity::class.java)
            intent.putExtra(Constant.Bundle.BookId, Integer.valueOf(mBookId))
            startActivity(intent)
        }

        read_tv_community.setOnClickListener {
            if (isVisible(read_ll_bottom_menu)) {
                if (isVisible(rlReadMark)) {
                    gone(rlReadMark)
                } else {
                    gone(ll_light)
                    updateMark()
                    visible(rlReadMark)
                }
            }
        }

        tvAddMark.setOnClickListener {
            if (mCurrentChapter != null) {
                mMarkAdapter!!.edit = false
                AccountManager.getInstance()
                    .addSign(mBookId, mCurrentChapter!!.chapterId, mCurrentChapter!!.title)
            }
        }

        tvClear.setOnClickListener {
            if (mMarkAdapter!!.edit) {
                val sign = mMarkAdapter!!.selectList
                if (sign != "") {
                    AccountManager.getInstance().deleteSign(sign)
                }
                mMarkAdapter!!.edit = false
            } else {
                mMarkAdapter!!.edit = true
            }
        }

        tv_cache.setOnClickListener {
            if (!isCollected) { //没有收藏 先收藏 然后弹框
                //设置为已收藏
                isCollected = true
                //设置阅读时间
                mCollBook!!.lastRead = System.currentTimeMillis().toString()
                BookRepository.getInstance().saveCollBookWithAsync(mCollBook!!)
            }
            showDownLoadDialog()

        }
        rlv_list.setOnItemClickListener { _, _, position, _ ->
            read_dl_slide.closeDrawer(GravityCompat.START)
            mPageLoader.skipToChapter(position)
        }
        iv_guide.setOnClickListener {
            iv_guide.visibility = GONE
            SpUtil.setBooleanValue(Constant.BookGuide, true)
        }
    }

    private fun showDownLoadDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.d_cache_num))
            .setItems(
                arrayOf(
                    getString(R.string.d_cache_last_50),
                    getString(R.string.d_cache_last_all),
                    getString(R.string.d_cache_all)
                )
            ) { _, which ->
                when (which) {
                    0 -> {
                        //50章
                        val last = currentChapter + 50
                        if (last > mCollBook!!.bookChapters.size) {
                            downLoadCache(mCollBook!!.bookChapters, mCollBook!!.bookChapters.size)
                        } else {
                            downLoadCache(mCollBook!!.bookChapters, last)
                        }
                    }
                    1 -> {
                        //后面所有
                        val lastBeans = ArrayList<BookChapterBean>()
                        for (i in currentChapter until mCollBook!!.bookChapters.size) {
                            lastBeans.add(mCollBook!!.bookChapters[i])
                        }
                        downLoadCache(lastBeans, mCollBook!!.bookChapters.size - currentChapter)
                    }
                    2 -> downLoadCache(mCollBook!!.bookChapters, mCollBook!!.bookChapters.size) //所有
                    else -> {
                    }
                }
                toggleMenu(true)
            }
        builder.show()
    }

    private fun downLoadCache(beans: List<BookChapterBean>, size: Int) {
        val task = DownloadTaskBean()
        task.taskName = mCollBook!!.title
        task.bookId = mCollBook!!.id
        task.bookChapters = beans //计算要缓存的章节
        task.currentChapter = currentChapter
        task.lastChapter = size

        RxBus.getInstance().post(task)
        startService(Intent(this, DownloadService::class.java))
    }

    private fun toggleNightMode() {
        if (isNightMode) {
            tvBookReadMode.text = resources.getString(R.string.book_read_mode_day)
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_moring)
            tvBookReadMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
            cl_layout.setBackgroundColor(ContextCompat.getColor(this, R.color.nb_read_bg_night))
        } else {
            tvBookReadMode.text = resources.getString(R.string.book_read_mode_day)
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_night)
            tvBookReadMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
            cl_layout.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    ReadSettingManager.getInstance().pageStyle.bgColor
                )
            )
        }
    }

    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private fun hideReadMenu(): Boolean {
        hideSystemBar()
        if (read_abl_top_menu.visibility == VISIBLE) {
            toggleMenu(true)
            return true
        } else if (mSettingDialog!!.isShowing) {
            mSettingDialog!!.dismiss()
            return true
        }
        return false
    }

    private fun showSystemBar() {
        //显示
        SystemBarUtils.showUnStableStatusBar(this)
        if (isFullScreen) {
            SystemBarUtils.showUnStableNavBar(this)
        }
    }

    private fun hideSystemBar() {
        //隐藏
        SystemBarUtils.hideStableStatusBar(this)
        if (isFullScreen) {
            SystemBarUtils.hideStableNavBar(this)
        }
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private fun toggleMenu(hideStatusBar: Boolean) {
        initMenuAnim()
        gone(ll_light, rlReadMark)
        if (read_abl_top_menu.visibility == VISIBLE) {
            //关闭
            read_abl_top_menu.startAnimation(mTopOutAnim)
            read_ll_bottom_menu.startAnimation(mBottomOutAnim)
            read_abl_top_menu.visibility = GONE
            read_ll_bottom_menu.visibility = GONE

            if (hideStatusBar) {
                hideSystemBar()
            }
        } else {
            read_abl_top_menu.visibility = VISIBLE
            read_ll_bottom_menu.visibility = VISIBLE
            read_abl_top_menu.startAnimation(mTopInAnim)
            read_ll_bottom_menu.startAnimation(mBottomInAnim)

            showSystemBar()
        }
    }

    //初始化菜单动画
    private fun initMenuAnim() {
        if (mTopInAnim != null) return
        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in)
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out)
        //退出的速度要快
        mTopOutAnim!!.duration = 200
        mBottomOutAnim!!.duration = 200
    }

    @Subscribe
    fun getBookArticle(event: BookArticleEvent) {
        Log.e(TAG, "getBookArticle: ")
        if (event.isFail) {

        } else {
            val chapterBeans = event.result!!.chapterBean
            mPageLoader.collBook.bookChapters = chapterBeans
            mPageLoader.refreshChapterList()

            // 如果是目录更新的情况，那么就需要存储更新数据
            if (mCollBook!!.isUpdate && isCollected) {
                BookRepository.getInstance()
                    .saveBookChaptersWithAsync(event.result!!.chapterBean, mCollBook!!)
            }
        }
    }

    @Subscribe
    fun finishChapter(event: FinishChapterEvent) {
        if (mPageLoader.pageStatus == PageLoader.STATUS_LOADING) {
            mHandler.sendEmptyMessage(WHAT_CHAPTER)
        }
        // 当完成章节的时候，刷新列表
        mCategoryAdapter!!.notifyDataSetChanged()
    }

    @Subscribe
    fun errorChapter(event: ErrorChapterEvent) {
        if (mPageLoader.pageStatus == PageLoader.STATUS_LOADING) {
            mPageLoader.chapterError()
        }
    }

    private fun updateMark() {
        AccountManager.getInstance().getSignList(mBookId)
    }

    @Subscribe
    fun addSign(event: AddBookSignEvent) {
        if (event.isFail) {
            ToastUtils.showNormalToast(this, "添加书签失败,请检查网络设置")
        } else {
            ToastUtils.showNormalToast(this, "添加书签成功")
            updateMark()
        }
    }

    @Subscribe
    fun deleteSigin(event: DeleteBookSignEvent) {
        if (event.isFail) {
            ToastUtils.showNormalToast(this, event.er!!.msg)
        } else {
            ToastUtils.showNormalToast(this, event.result!!.msg)
            updateMark()
        }
    }

    @Subscribe
    fun getSignList(event: GetBookSignEvent) {
        if (event.isFail) {
            ToastUtils.showNormalToast(this, "获取书签失败,请检查网络设置")
        } else {
            mMarks.clear()
            mMarks.addAll(event.result!!.sign)
            mMarkAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> return mPageLoader.skipToPrePage()

            KeyEvent.KEYCODE_VOLUME_DOWN -> return mPageLoader.skipToNextPage()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (read_abl_top_menu.visibility == VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.getInstance().isFullScreen) {
                toggleMenu(true)
                return
            }
        } else if (mSettingDialog!!.isShowing) {
            mSettingDialog!!.dismiss()
            return
        } else if (read_dl_slide.isDrawerOpen(GravityCompat.START)) {
            read_dl_slide.closeDrawer(GravityCompat.START)
            return
        }
        Log.e(TAG, "onBackPressed: " + mCollBook!!.bookChapters.isEmpty())

        if (!mCollBook!!.isLocal && !isCollected && mCollBook!!.bookChapters.isNotEmpty()) {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_book))
                .setMessage(getString(R.string.like_book))
                .setPositiveButton(getString(R.string.sure)) { dialog, which ->
                    //设置为已收藏
                    isCollected = true
                    //设置阅读时间
                    mCollBook!!.lastRead = System.currentTimeMillis().toString()

                    BookRepository.getInstance().saveCollBookWithAsync(mCollBook!!)

                    exit()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, which -> exit() }.create()
            alertDialog.show()
        } else {
            exit()
        }
    }

    // 退出
    private fun exit() {
        // 返回给BookDetail。
        val result = Intent()
        result.putExtra(RESULT_IS_COLLECTED, isCollected)
        setResult(Activity.RESULT_OK, result)
        // 退出
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        if (isCollected) {
            mPageLoader.saveRecord()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.instance.unregisterSubscriber(this)
        mPageLoader.closeBook()
//        mPageLoader = null
        unbindService(mConn)
        unregisterReceiver(mReceiver)
    }

    private fun initService() {

        mConn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                mService = service as DownloadService.IDownloadManager
                mService!!.setOnDownloadListener(this@NovelReadActivity)
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        }
        //绑定
        bindService(Intent(this, DownloadService::class.java), mConn, Service.BIND_AUTO_CREATE)
    }

    override fun onDownloadChange(pos: Int, status: Int, msg: String) {
        //        DownloadTaskBean bean = mDownloadAdapter.getItem(pos);
        //        bean.setStatus(status);
        //        if (DownloadTaskBean.STATUS_LOADING == status){
        //            bean.setCurrentChapter(Integer.valueOf(msg));
        //        }
        //        mDownloadAdapter.notifyItemChanged(pos);
        Log.e(TAG, "onDownloadChange: $pos $status $msg")

        if (msg == getString(R.string.download_success) || msg == getString(R.string.download_error)) {
            //下载成功或失败后隐藏下载视图
            if (ll_download != null) {
                ll_download.visibility = GONE
                ToastUtils.showNormalToast(this, msg)
            }
        } else {
            if (ll_download != null) {
                ll_download.visibility = VISIBLE
                tv_progress.text = getString(
                    R.string.download_loading,
                    mService!!.downloadTaskList[pos].currentChapter,
                    mService!!.downloadTaskList[pos].lastChapter
                )
                pb_loading.max = mService!!.downloadTaskList[pos].lastChapter
                pb_loading.progress = mService!!.downloadTaskList[pos].currentChapter
            }
        }
    }


    override fun onDownloadResponse(pos: Int, status: Int) {
        //        DownloadTaskBean bean = mDownloadAdapter.getItem(pos);
        //        bean.setStatus(status);
        //        mDownloadAdapter.notifyItemChanged(pos);
        Log.e(TAG, "onDownloadResponse: $pos $status")
    }

    @Subscribe
    fun onDownLoadEvent(message: DownloadMessage) {
        ToastUtils.showNormalToast(this, message.message)
    }

    companion object {

        private const val TAG = "NovelReadActivity"
        const val EXTRA_COLL_BOOK = "extra_coll_book"
        const val EXTRA_IS_COLLECTED = "extra_is_collected"
        private const val WHAT_CATEGORY = 1
        private const val WHAT_CHAPTER = 2
    }
}
