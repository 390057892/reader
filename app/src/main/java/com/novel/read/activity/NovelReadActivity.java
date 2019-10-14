package com.novel.read.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.common_lib.base.utils.ToastUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.mango.mangolib.event.EventManager;
import com.novel.read.BuildConfig;
import com.novel.read.R;
import com.novel.read.adapter.CategoryAdapter;
import com.novel.read.adapter.MarkAdapter;
import com.novel.read.base.MyApp;
import com.novel.read.base.NovelBaseActivity;
import com.novel.read.constants.Constant;
import com.novel.read.event.AddBookSignEvent;
import com.novel.read.event.BookArticleEvent;
import com.novel.read.event.DeleteBookSignEvent;
import com.novel.read.event.ErrorChapterEvent;
import com.novel.read.event.FinishChapterEvent;
import com.novel.read.event.GetBookSignEvent;
import com.novel.read.event.RxBus;
import com.novel.read.event.SetAdsBgEvent;
import com.novel.read.http.AccountManager;
import com.novel.read.model.db.BookChapterBean;
import com.novel.read.model.db.CollBookBean;
import com.novel.read.model.db.DownloadTaskBean;
import com.novel.read.model.db.dbManage.BookRepository;
import com.novel.read.model.protocol.MarkResp;
import com.novel.read.service.DownloadMessage;
import com.novel.read.service.DownloadService;
import com.novel.read.utlis.BrightnessUtils;
import com.novel.read.utlis.ScreenUtils;
import com.novel.read.utlis.SpUtil;
import com.novel.read.utlis.SystemBarUtils;
import com.novel.read.widget.dialog.ReadSettingDialog;
import com.novel.read.widget.page.PageLoader;
import com.novel.read.widget.page.PageView;
import com.novel.read.widget.page.ReadSettingManager;
import com.novel.read.widget.page.TxtChapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

import static android.view.View.GONE;
import static android.view.View.LAYER_TYPE_SOFTWARE;
import static android.view.View.VISIBLE;
import static com.novel.read.constants.Constant.ResultCode.RESULT_IS_COLLECTED;

public class NovelReadActivity extends NovelBaseActivity implements DownloadService.OnDownloadListener {

    @BindView(R.id.read_pv_page)
    PageView mPvPage;
    @BindView(R.id.read_abl_top_menu)
    AppBarLayout mAblTopMenu;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.read_ll_bottom_menu)
    LinearLayoutCompat mLlBottomMenu;

    @BindView(R.id.read_dl_slide)
    DrawerLayout mDlSlide;
    @BindView(R.id.read_tv_category)
    TextView mTvCategory;
    @BindView(R.id.tv_light)
    TextView mTvLight;
    @BindView(R.id.read_setting_sb_brightness)
    SeekBar mSbBrightness;
    @BindView(R.id.tvBookReadMode)
    TextView mTvNightMode;
    @BindView(R.id.ll_light)
    LinearLayoutCompat mLLight;
    @BindView(R.id.tv_cache)
    TextView mTvCache;
    @BindView(R.id.tv_setting)
    TextView mTvSetting;
    @BindView(R.id.rlv_list)
    ListView mLvCategory;
    @BindView(R.id.tv_book_name)
    TextView mTvBookName;
    @BindView(R.id.read_tv_brief)
    TextView mTvBrief;
    @BindView(R.id.read_tv_community)
    TextView mTvMark;
    @BindView(R.id.rlReadMark)
    ConstraintLayout rlReadMark;
    @BindView(R.id.tvAddMark)
    TextView mTvAddMark;
    @BindView(R.id.tvClear)
    TextView mTvClear;
    @BindView(R.id.rlv_mark)
    RecyclerView mRlvMark;
    @BindView(R.id.ll_download)
    LinearLayoutCompat mLlDownLoad;
    @BindView(R.id.pb_loading)
    ContentLoadingProgressBar loadingProgressBar;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;
    @BindView(R.id.cl_layout)
    ConstraintLayout mClLayout;
    @BindView(R.id.iv_guide)
    ImageView mIvGuide;
    private CategoryAdapter mCategoryAdapter;
    private List<TxtChapter> mChapters = new ArrayList<>();
    private TxtChapter mCurrentChapter; //当前章节
    private int currentChapter = 0;
    private MarkAdapter mMarkAdapter;
    private List<MarkResp.SignBean> mMarks = new ArrayList<>();
    private PageLoader mPageLoader;
    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;

    private ReadSettingDialog mSettingDialog;
    private boolean isCollected = false; // isFromSDCard
    private boolean isNightMode = false;
    private boolean isFullScreen = false;
    private boolean isRegistered = false;

    private CollBookBean mCollBook;
    private String mBookId;

    private static final String TAG = "NovelReadActivity";
    public static final String EXTRA_COLL_BOOK = "extra_coll_book";
    public static final String EXTRA_IS_COLLECTED = "extra_is_collected";
    private static final int WHAT_CATEGORY = 1;
    private static final int WHAT_CHAPTER = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_CATEGORY:
                    mLvCategory.setSelection(mPageLoader.getChapterPos());
                    break;
                case WHAT_CHAPTER:
                    mPageLoader.openChapter();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read;
    }

    @Override
    protected void initView() {
        EventManager.Companion.getInstance().registerSubscriber(this);
        mCollBook = (CollBookBean) getIntent().getSerializableExtra(EXTRA_COLL_BOOK);
        isCollected = getIntent().getBooleanExtra(EXTRA_IS_COLLECTED, false);
        mBookId = mCollBook.getId();
        initService();
        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mPvPage.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //获取页面加载器
        mPageLoader = mPvPage.getPageLoader(mCollBook);

        mSettingDialog = new ReadSettingDialog(this, mPageLoader);
        //禁止滑动展示DrawerLayout
        mDlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //侧边打开后，返回键能够起作用
        mDlSlide.setFocusableInTouchMode(false);
        //半透明化StatusBar
        SystemBarUtils.transparentStatusBar(this);
        //隐藏StatusBar
        mPvPage.post(
                this::hideSystemBar
        );
        mAblTopMenu.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
        mLlDownLoad.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, ScreenUtils.dpToPx(15));

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = 1;
        }
        getWindow().setAttributes(lp);

        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto()) {
            BrightnessUtils.setDefaultBrightness(this);
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().getBrightness());
        }

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, intentFilter);

        if (!SpUtil.getBooleanValue(Constant.BookGuide,false)){
            mIvGuide.setVisibility(VISIBLE);
            toggleMenu(false);
        }

        Log.e(TAG, "mBookId: " + mBookId);
        if (isCollected) {
            mPageLoader.getCollBook().setBookChapters(BookRepository.getInstance().getBookChaptersInRx(mBookId));
            // 刷新章节列表
            mPageLoader.refreshChapterList();
            // 如果是网络小说并被标记更新的，则从网络下载目录
            if (mCollBook.isUpdate() && !mCollBook.isLocal()) {
                AccountManager.getInstance().getBookArticle(mBookId, "2", "1", "10000");
            }
        } else {
            AccountManager.getInstance().getBookArticle(mBookId, "2", "1", "10000");
        }



        setAdsBg(new SetAdsBgEvent());

    }

    @Override
    protected void initData() {
        mTvBookName.setText(mCollBook.getTitle());
        mCategoryAdapter = new CategoryAdapter();
        mLvCategory.setAdapter(mCategoryAdapter);
        mLvCategory.setFastScrollEnabled(true);
        mRlvMark.setLayoutManager(new LinearLayoutManager(this));
        mMarkAdapter = new MarkAdapter(mMarks);
        mRlvMark.setAdapter(mMarkAdapter);
        isNightMode = ReadSettingManager.getInstance().isNightMode();
        //夜间模式按钮的状态
        toggleNightMode();
        isFullScreen = ReadSettingManager.getInstance().isFullScreen();
        toolbar.setNavigationOnClickListener(view -> finish());
        mSbBrightness.setProgress(ReadSettingManager.getInstance().getBrightness());
        mPageLoader.setOnPageChangeListener(
                new PageLoader.OnPageChangeListener() {

                    @Override
                    public void onChapterChange(int pos) {
                        mCategoryAdapter.setChapter(pos);
                        mCurrentChapter = mChapters.get(pos);
                        currentChapter = pos;
                    }

                    @Override
                    public void requestChapters(List<TxtChapter> requestChapters) {
                        AccountManager.getInstance().getBookArticleDetail(mBookId, requestChapters);
                        mHandler.sendEmptyMessage(WHAT_CATEGORY);
                    }

                    @Override
                    public void onCategoryFinish(List<TxtChapter> chapters) {
                        mChapters.clear();
                        mChapters.addAll(chapters);
                        mCategoryAdapter.refreshItems(mChapters);
                    }

                    @Override
                    public void onPageCountChange(int count) {
                    }

                    @Override
                    public void onPageChange(int pos) {

                    }
                }
        );
        mPvPage.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                return !hideReadMenu();
            }

            @Override
            public void center() {
                toggleMenu(true);
            }

            @Override
            public void prePage() {
            }

            @Override
            public void nextPage() {
            }

            @Override
            public void cancel() {
            }
        });
        mTvCategory.setOnClickListener(
                (v) -> {
                    //移动到指定位置
                    if (mCategoryAdapter.getCount() > 0) {
                        mLvCategory.setSelection(mPageLoader.getChapterPos());
                    }
                    //切换菜单
                    toggleMenu(true);
                    //打开侧滑动栏
                    mDlSlide.openDrawer(GravityCompat.START);
                }
        );
        mTvLight.setOnClickListener(view -> {
            mLLight.setVisibility(GONE);
            rlReadMark.setVisibility(GONE);
            if (isVisible(mLLight)) {
                mLLight.setVisibility(GONE);
            } else {
                mLLight.setVisibility(VISIBLE);
            }
        });
        mTvSetting.setOnClickListener(view -> {
            mLLight.setVisibility(GONE);
            rlReadMark.setVisibility(GONE);
            toggleMenu(false);
            mSettingDialog.show();
        });

        mSbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                //设置当前 Activity 的亮度
                BrightnessUtils.setBrightness(NovelReadActivity.this, progress);
                //存储亮度的进度条
                ReadSettingManager.getInstance().setBrightness(progress);
            }
        });

        mTvNightMode.setOnClickListener(
                (v) -> {
                    isNightMode = !isNightMode;
                    mPageLoader.setNightMode(isNightMode);
                    toggleNightMode();
                }
        );

        mTvBrief.setOnClickListener(view -> {
            Intent intent = new Intent(this, NovelBookDetailActivity.class);
            intent.putExtra(Constant.Bundle.BookId, Integer.valueOf(mBookId));
            startActivity(intent);
        });

        mTvMark.setOnClickListener(view -> {
            if (isVisible(mLlBottomMenu)) {
                if (isVisible(rlReadMark)) {
                    gone(rlReadMark);
                } else {
                    gone(mLLight);
                    updateMark();
                    visible(rlReadMark);
                }
            }
        });

        mTvAddMark.setOnClickListener(view -> {
            if (mCurrentChapter != null) {
                mMarkAdapter.setEdit(false);
                AccountManager.getInstance().addSign(mBookId, mCurrentChapter.getChapterId(), mCurrentChapter.getTitle());
            }
        });

        mTvClear.setOnClickListener(view -> {
            if (mMarkAdapter.getEdit()) {
                String sign = mMarkAdapter.getSelectList();
                if (!sign.equals("")) {
                    AccountManager.getInstance().deleteSign(sign);
                }
                mMarkAdapter.setEdit(false);
            } else {
                mMarkAdapter.setEdit(true);
            }
        });

        mTvCache.setOnClickListener(view -> {
            if (!isCollected) { //没有收藏 先收藏 然后弹框
                //设置为已收藏
                isCollected = true;
                //设置阅读时间
                mCollBook.setLastRead(String.valueOf(System.currentTimeMillis()));
                BookRepository.getInstance().saveCollBookWithAsync(mCollBook);
            }
            showDownLoadDialog();

        });
        mLvCategory.setOnItemClickListener((parent, view, position, id) -> {
            mDlSlide.closeDrawer(GravityCompat.START);
            mPageLoader.skipToChapter(position);
        });
        mIvGuide.setOnClickListener(view -> {
            mIvGuide.setVisibility(GONE);
            SpUtil.setBooleanValue(Constant.BookGuide, true);
        });
    }

    private void showDownLoadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.d_cache_num))
                .setItems(new String[]{getString(R.string.d_cache_last_50), getString(R.string.d_cache_last_all), getString(R.string.d_cache_all)}, (dialog, which) -> {
                    switch (which) {
                        case 0: //50章
                            int last = currentChapter + 50;
                            if (last > mCollBook.getBookChapters().size()) {
                                downLoadCache(mCollBook.getBookChapters(), mCollBook.getBookChapters().size());
                            } else {
                                downLoadCache(mCollBook.getBookChapters(), last);
                            }

                            break;
                        case 1: //后面所有
                            List<BookChapterBean> lastBeans = new ArrayList<>();
                            for (int i = currentChapter; i < mCollBook.getBookChapters().size(); i++) {
                                lastBeans.add(mCollBook.getBookChapters().get(i));
                            }
                            downLoadCache(lastBeans, mCollBook.getBookChapters().size() - currentChapter);
                            break;
                        case 2: //所有
                            downLoadCache(mCollBook.getBookChapters(), mCollBook.getBookChapters().size());
                            break;
                        default:
                            break;
                    }
                    toggleMenu(true);
                });
        builder.show();
    }

    private void downLoadCache(List<BookChapterBean> beans, int size) {
        DownloadTaskBean task = new DownloadTaskBean();
        task.setTaskName(mCollBook.getTitle());
        task.setBookId(mCollBook.getId());
        task.setBookChapters(beans); //计算要缓存的章节
        task.setCurrentChapter(currentChapter);
        task.setLastChapter(size);

        RxBus.getInstance().post(task);
        startService(new Intent(this, DownloadService.class));
    }

    private void toggleNightMode() {
        if (isNightMode) {
            mTvNightMode.setText(getResources().getString(R.string.book_read_mode_day));
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_moring);
            mTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            mClLayout.setBackgroundColor(getResources().getColor(R.color.nb_read_bg_night));
        } else {
            mTvNightMode.setText(getResources().getString(R.string.book_read_mode_day));
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_night);
            mTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            mClLayout.setBackgroundColor(getResources().getColor(ReadSettingManager.getInstance().getPageStyle().getBgColor()));
        }
    }

    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private boolean hideReadMenu() {
        hideSystemBar();
        if (mAblTopMenu.getVisibility() == VISIBLE) {
            toggleMenu(true);
            return true;
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return true;
        }
        return false;
    }

    private void showSystemBar() {
        //显示
        SystemBarUtils.showUnStableStatusBar(this);
        if (isFullScreen) {
            SystemBarUtils.showUnStableNavBar(this);
        }
    }

    private void hideSystemBar() {
        //隐藏
        SystemBarUtils.hideStableStatusBar(this);
        if (isFullScreen) {
            SystemBarUtils.hideStableNavBar(this);
        }
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private void toggleMenu(boolean hideStatusBar) {
        initMenuAnim();
        gone(mLLight, rlReadMark);
        if (mAblTopMenu.getVisibility() == View.VISIBLE) {
            //关闭
            mAblTopMenu.startAnimation(mTopOutAnim);
            mLlBottomMenu.startAnimation(mBottomOutAnim);
            mAblTopMenu.setVisibility(GONE);
            mLlBottomMenu.setVisibility(GONE);

            if (hideStatusBar) {
                hideSystemBar();
            }
        } else {
            mAblTopMenu.setVisibility(View.VISIBLE);
            mLlBottomMenu.setVisibility(View.VISIBLE);
            mAblTopMenu.startAnimation(mTopInAnim);
            mLlBottomMenu.startAnimation(mBottomInAnim);

            showSystemBar();
        }
    }

    //初始化菜单动画
    private void initMenuAnim() {
        if (mTopInAnim != null) return;
        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        //退出的速度要快
        mTopOutAnim.setDuration(200);
        mBottomOutAnim.setDuration(200);
    }

    @Subscribe
    public void getBookArticle(BookArticleEvent event) {
        Log.e(TAG, "getBookArticle: ");
        if (event.isFail()) {

        } else {
            List<BookChapterBean> chapterBeans = event.getResult().getChapterBean();
            mPageLoader.getCollBook().setBookChapters(chapterBeans);
            mPageLoader.refreshChapterList();

            // 如果是目录更新的情况，那么就需要存储更新数据
            if (mCollBook.isUpdate() && isCollected) {
                BookRepository.getInstance().saveBookChaptersWithAsync(event.getResult().getChapterBean(), mCollBook);
            }
        }
    }

    @Subscribe
    public void finishChapter(FinishChapterEvent event) {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mHandler.sendEmptyMessage(WHAT_CHAPTER);
        }
        // 当完成章节的时候，刷新列表
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void errorChapter(ErrorChapterEvent event) {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mPageLoader.chapterError();
        }
    }

    private void updateMark() {
        AccountManager.getInstance().getSignList(mBookId);
    }

    @Subscribe
    public void addSign(AddBookSignEvent event) {
        if (event.isFail()) {
            ToastUtils.showNormalToast(this, "添加书签失败,请检查网络设置");
        } else {
            ToastUtils.showNormalToast(this, "添加书签成功");
            updateMark();
        }
    }

    @Subscribe
    public void deleteSigin(DeleteBookSignEvent event) {
        if (event.isFail()) {
            ToastUtils.showNormalToast(this, event.getEr().getMsg());
        } else {
            ToastUtils.showNormalToast(this, event.getResult().getMsg());
            updateMark();
        }
    }

    @Subscribe
    public void getSignList(GetBookSignEvent event) {
        if (event.isFail()) {
            ToastUtils.showNormalToast(this, "获取书签失败,请检查网络设置");
        } else {
            mMarks.clear();
            mMarks.addAll(event.getResult().getSign());
            mMarkAdapter.notifyDataSetChanged();
        }
    }

    // 接收电池信息和时间更新的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                mPageLoader.updateBattery(level);
            }
            // 监听分钟的变化
            else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                mPageLoader.updateTime();
            }
        }
    };

    @Subscribe
    public void setAdsBg(SetAdsBgEvent event) {
        mClLayout.setBackgroundColor(getResources().getColor(ReadSettingManager.getInstance().getPageStyle().getBgColor()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                return mPageLoader.skipToPrePage();

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return mPageLoader.skipToNextPage();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mAblTopMenu.getVisibility() == View.VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.getInstance().isFullScreen()) {
                toggleMenu(true);
                return;
            }
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return;
        }else if (mDlSlide.isDrawerOpen(GravityCompat.START)) {
            mDlSlide.closeDrawer(GravityCompat.START);
            return;
        }
        Log.e(TAG, "onBackPressed: " + mCollBook.getBookChapters().isEmpty());

        if (!mCollBook.isLocal() && !isCollected && !mCollBook.getBookChapters().isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.add_book))
                    .setMessage(getString(R.string.like_book))
                    .setPositiveButton(getString(R.string.sure), (dialog, which) -> {
                        //设置为已收藏
                        isCollected = true;
                        //设置阅读时间
                        mCollBook.setLastRead(String.valueOf(System.currentTimeMillis()));

                        BookRepository.getInstance().saveCollBookWithAsync(mCollBook);

                        exit();
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> exit()).create();
            alertDialog.show();
        } else {
            exit();
        }
    }

    // 退出
    private void exit() {
        // 返回给BookDetail。
        Intent result = new Intent();
        result.putExtra(RESULT_IS_COLLECTED, isCollected);
        setResult(Activity.RESULT_OK, result);
        // 退出
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCollected) {
            mPageLoader.saveRecord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
        mPageLoader.closeBook();
        mPageLoader = null;
        unbindService(mConn);
        unregisterReceiver(mReceiver);
    }

    private DownloadService.IDownloadManager mService;
    private ServiceConnection mConn;

    private void initService() {

        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = (DownloadService.IDownloadManager) service;
                mService.setOnDownloadListener(NovelReadActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        //绑定
        bindService(new Intent(this, DownloadService.class), mConn, Service.BIND_AUTO_CREATE);
    }

    @Override
    public void onDownloadChange(int pos, int status, String msg) {
//        DownloadTaskBean bean = mDownloadAdapter.getItem(pos);
//        bean.setStatus(status);
//        if (DownloadTaskBean.STATUS_LOADING == status){
//            bean.setCurrentChapter(Integer.valueOf(msg));
//        }
//        mDownloadAdapter.notifyItemChanged(pos);
        Log.e(TAG, "onDownloadChange: " + pos + " " + status + " " + msg);

        if (msg.equals(getString(R.string.download_success)) || msg.equals(getString(R.string.download_error))) {
            //下载成功或失败后隐藏下载视图
            if (mLlDownLoad != null) {
                mLlDownLoad.setVisibility(GONE);
                ToastUtils.showNormalToast(this, msg);
            }
        } else {
            if (mLlDownLoad != null) {
                mLlDownLoad.setVisibility(VISIBLE);
                mTvProgress.setText(getString(R.string.download_loading,
                        mService.getDownloadTaskList().get(pos).getCurrentChapter(),
                        mService.getDownloadTaskList().get(pos).getLastChapter()));
                loadingProgressBar.setMax(mService.getDownloadTaskList().get(pos).getLastChapter());
                loadingProgressBar.setProgress(mService.getDownloadTaskList().get(pos).getCurrentChapter());
            }
        }
    }


    @Override
    public void onDownloadResponse(int pos, int status) {
//        DownloadTaskBean bean = mDownloadAdapter.getItem(pos);
//        bean.setStatus(status);
//        mDownloadAdapter.notifyItemChanged(pos);
        Log.e(TAG, "onDownloadResponse: " + pos + " " + status);
    }

    @Subscribe
    public void onDownLoadEvent(DownloadMessage message) {
        ToastUtils.showNormalToast(this, message.message);
    }
}
