package com.novel.read.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.common_lib.base.utils.ToastUtils;
import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.adapter.LoveLyAdapter;
import com.novel.read.base.NovelBaseActivity;
import com.novel.read.constants.Constant;
import com.novel.read.event.BookArticleEvent;
import com.novel.read.event.GetBookDetailEvent;
import com.novel.read.event.GetRecommendBookEvent;
import com.novel.read.event.UpdateBookEvent;
import com.novel.read.http.AccountManager;
import com.novel.read.model.db.BookChapterBean;
import com.novel.read.model.db.CollBookBean;
import com.novel.read.model.db.dbManage.BookRepository;
import com.novel.read.model.protocol.BookDetailResp;
import com.novel.read.model.protocol.RecommendBookResp;
import com.novel.read.utlis.DateUtli;
import com.novel.read.utlis.GlideImageLoader;
import com.novel.read.widget.RefreshLayout;
import com.squareup.otto.Subscribe;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.novel.read.constants.Constant.RequestCode.REQUEST_READ;
import static com.novel.read.constants.Constant.ResultCode.RESULT_IS_COLLECTED;

public class NovelBookDetailActivity extends NovelBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_book)
    ImageView mIvBook;
    @BindView(R.id.tv_book_name)
    TextView mTvBookName;
    @BindView(R.id.tv_book_author)
    TextView mTvBookAuthor;
    @BindView(R.id.tv_book_length)
    TextView mTvBookLength;
    @BindView(R.id.tv_new_title)
    TextView mTvNewTitle;
    @BindView(R.id.tv_update_time)
    TextView mTvUpdateTime;
    @BindView(R.id.tv_human_num)
    TextView mTvHumanNum;
    @BindView(R.id.tv_love_look_num)
    TextView mTvLoveLookNum;
    @BindView(R.id.tv_Intro)
    TextView mTvIntro;
    @BindView(R.id.rlv_lovely)
    RecyclerView mRlvLovely;
    @BindView(R.id.tv_add_book)
    TextView mTvAddBook;
    @BindView(R.id.tv_start_read)
    TextView mTvStartRead;
    @BindView(R.id.refresh)
    RefreshLayout refreshLayout;

    private LoveLyAdapter mAdapter;
    private List<RecommendBookResp.BookBean> mList = new ArrayList<>();

    private int mBookId;

    private boolean isCollected = false;
    private CollBookBean mCollBookBean;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_book_detail;
    }

    @Override
    protected void initView() {
        mBookId = getIntent().getIntExtra(Constant.Bundle.BookId,0);
        mRlvLovely.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new LoveLyAdapter(mList);
        mRlvLovely.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        refreshLayout.showLoading();
        refreshLayout.setOnReloadingListener(new RefreshLayout.OnReloadingListener() {
            @Override
            public void onReload() {
                getData();
            }
        });
        getData();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getData(){
        AccountManager.getInstance().getRecommendBook(String.valueOf(mBookId), "10");
        AccountManager.getInstance().getBookDetail(String.valueOf(mBookId));
    }

    @OnClick({R.id.tv_add_book, R.id.tv_start_read})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_add_book:
                //点击存储
                if (isCollected) {
                    //放弃点击
                    BookRepository.getInstance().deleteCollBookInRx(mCollBookBean);
                    mTvAddBook.setText(getResources().getString(R.string.add_book));
                    isCollected = false;
                } else {
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressDialog(this);
                        mProgressDialog.setTitle("正在添加到书架中");
                    }
                    mProgressDialog.show();
                    AccountManager.getInstance().getBookArticle(String.valueOf(mBookId),"2","1","100000");

                }
                break;
            case R.id.tv_start_read:
//                if (mCollBookBean.getInclude_image()==Constant.HasImage.has){
//                    startActivityForResult(new Intent(this, NovelWebReadActivity.class)
//                            .putExtra(NovelReadActivity.EXTRA_IS_COLLECTED, isCollected)
//                            .putExtra(NovelReadActivity.EXTRA_COLL_BOOK, mCollBookBean), REQUEST_READ);
//                }else {
                    startActivityForResult(new Intent(this, NovelReadActivity.class)
                            .putExtra(NovelReadActivity.EXTRA_IS_COLLECTED, isCollected)
                            .putExtra(NovelReadActivity.EXTRA_COLL_BOOK, mCollBookBean), REQUEST_READ);
//                }
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void getBookDetail(GetBookDetailEvent event) {
        refreshLayout.showFinish();
        if (event.isFail()) {
            refreshLayout.showError();
        } else {
            BookDetailResp.BookBean bookBean = event.getResult().getBook();
            GlideImageLoader.displayCornerImage(this, bookBean.getCover(), mIvBook);
            mTvBookName.setText(bookBean.getTitle());

            mTvBookAuthor.setText(bookBean.getAuthor() + " | ");
            mTvBookLength.setText(getString(R.string.book_word, bookBean.getWords() / 10000));

            if (event.getResult().getLast_article()!=null){
                mTvNewTitle.setText(getString(R.string.new_chapter,event.getResult().getLast_article().getTitle()));
                mTvUpdateTime.setText(DateUtli.dateConvert(event.getResult().getLast_article().getCreate_time(),0));
            }

            mTvHumanNum.setText(bookBean.getHot() + "");
            mTvLoveLookNum.setText(bookBean.getLike());
            mTvIntro.setText(bookBean.getDescription());
            mCollBookBean = BookRepository.getInstance().getCollBook(String.valueOf(bookBean.getId()));
            //判断是否收藏
            if (mCollBookBean != null) {
                isCollected = true;
                mTvAddBook.setText(getResources().getString(R.string.already_add));
                mTvStartRead.setText("继续阅读");
            } else {
                mCollBookBean = event.getResult().getCollBookBean();
            }
        }
    }

    @Subscribe
    public void getRecommendBook(GetRecommendBookEvent event) {
        if (event.isFail()) {

        } else {
            mList.clear();
            mList.addAll(event.getResult().getBook());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventManager.Companion.getInstance().registerSubscriber(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
    }

    @Subscribe
    public void getArticle(BookArticleEvent event){

        if (event.isFail()){
            dismiss();
            ToastUtils.showNormalToast(this,getString(R.string.net_error));
        }else {
            //存储收藏
            boolean success = false;
            if (mCollBookBean!=null){
                success = mCollBookBean.saveOrUpdate("bookId=?",mCollBookBean.getId());
            }
            if (success){
                List<BookChapterBean> bookChapterBean = event.getResult().getChapterBean();
                for (int i = 0; i <bookChapterBean.size() ; i++) {
                    bookChapterBean.get(i).setCollBookBean(mCollBookBean);
                }
                LitePal.saveAllAsync(bookChapterBean).listen(success1 -> {
                    if (success1) {
                        if (mTvAddBook!=null) {
                            mTvAddBook.setText(getResources().getString(R.string.already_add));
                        }
                        isCollected = true;
                    }else {
                        LitePal.deleteAll(CollBookBean.class,"bookId =?",mCollBookBean.getId());
                        ToastUtils.showNormalToast(this,getString(R.string.net_error));
                    }
                    dismiss();
                });
            }else {
                ToastUtils.showNormalToast(this,getString(R.string.net_error));
                dismiss();
            }
        }

    }

    private void dismiss(){
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果进入阅读页面收藏了，页面结束的时候，就需要返回改变收藏按钮
        if (requestCode == REQUEST_READ) {
            if (data == null) {
                return;
            }

            isCollected = data.getBooleanExtra(RESULT_IS_COLLECTED, false);

            if (isCollected) {
                mTvAddBook.setText(getResources().getString(R.string.already_add));
                mTvStartRead.setText("继续阅读");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.Companion.getInstance().postEvent(new UpdateBookEvent());
    }
}
