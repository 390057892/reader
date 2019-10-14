package com.novel.read.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.adapter.HistoryAdapter;
import com.novel.read.adapter.HotAdapter;
import com.novel.read.adapter.SearchAdapter;
import com.novel.read.base.NovelBaseActivity;
import com.novel.read.event.HotSearchEvent;
import com.novel.read.event.SearchListEvent;
import com.novel.read.http.AccountManager;
import com.novel.read.model.db.SearchListTable;
import com.novel.read.model.protocol.SearchResp;
import com.novel.read.utlis.DialogUtils;
import com.novel.read.widget.HeadLayout;
import com.novel.read.widget.RefreshLayout;
import com.spreada.utils.chinese.ZHConverter;
import com.squareup.otto.Subscribe;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.novel.read.constants.Constant.COMMENT_SIZE;

public class NovelSearchActivity extends NovelBaseActivity {

    @BindView(R.id.tv_search)
    EditText mTvSearch;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.head_hot)
    HeadLayout headHot;
    @BindView(R.id.rlv_hot)
    RecyclerView mRlvHot;
    @BindView(R.id.head_history)
    HeadLayout headHistory;
    @BindView(R.id.rlv_history)
    RecyclerView mRlvHistory;
    @BindView(R.id.rlv_search)
    RecyclerView mRlvSearch;
    @BindView(R.id.refresh)
    RefreshLayout refreshLayout;

    private List<String> mHotList = new ArrayList<>();
    private HotAdapter mHotAdapter;

    private List<SearchListTable> mHisList = new ArrayList<>();
    private HistoryAdapter mHisAdapter;

    private List<SearchResp.BookBean> mSearchList = new ArrayList<>();
    private SearchAdapter mSearchAdapter;

    private int page = 1;
    private int loadSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        EventManager.Companion.getInstance().registerSubscriber(this);

        FlexboxLayoutManager manager = new FlexboxLayoutManager(this);
        //设置主轴排列方式
        manager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setAlignItems(AlignItems.STRETCH);
        mRlvHot.setLayoutManager(manager);
        mHotAdapter = new HotAdapter(mHotList);
        mRlvHot.setAdapter(mHotAdapter);

        mHisList = LitePal.order("saveTime desc").limit(5).find(SearchListTable.class);
        FlexboxLayoutManager manager2 = new FlexboxLayoutManager(this);
        //设置主轴排列方式
        manager2.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        manager2.setFlexWrap(FlexWrap.WRAP);
        manager2.setAlignItems(AlignItems.STRETCH);
        mHisAdapter = new HistoryAdapter(mHisList);
        mRlvHistory.setLayoutManager(manager2);
        mRlvHistory.setAdapter(mHisAdapter);

        mRlvSearch.setLayoutManager(new LinearLayoutManager(this));
        mSearchAdapter = new SearchAdapter(mSearchList, mRlvSearch);
        mRlvSearch.setAdapter(mSearchAdapter);
        mSearchAdapter.setOnLoadMoreListener(() -> {
            if (mSearchAdapter.isLoadingMore()) {

            } else {
                if (loadSize >= COMMENT_SIZE) {
                    mSearchAdapter.setLoadingMore(true);
                    mSearchList.add(null);
                    mSearchAdapter.notifyDataSetChanged();
                    page++;
                    getData();
                }
            }
        });
        AccountManager.getInstance().getHotSearch();
    }

    private void getData() {
        String str = convertCC(mTvSearch.getText().toString().trim());
        AccountManager.getInstance().getSearchBookList("", str, page);
    }

    //繁簡轉換
    public String convertCC(String input) {
        if (TextUtils.isEmpty(input) || input.length() == 0)
            return "";
        return ZHConverter.getInstance(ZHConverter.SIMPLIFIED).convert(input);
    }


    @Override
    protected void initData() {
        //输入框
        mTvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equals("")) {
                    refreshLayout.setVisibility(View.GONE);
                    headHot.setVisibility(View.VISIBLE);
                    headHistory.setVisibility(View.VISIBLE);
                    mRlvHot.setVisibility(View.VISIBLE);
                    mRlvHistory.setVisibility(View.VISIBLE);
                } else {
                    refreshLayout.setVisibility(View.VISIBLE);
                    headHot.setVisibility(View.GONE);
                    headHistory.setVisibility(View.GONE);
                    mRlvHot.setVisibility(View.GONE);
                    mRlvHistory.setVisibility(View.GONE);
                    refreshLayout.showLoading();
                    page = 1;
                    getData();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //键盘的搜索
        mTvSearch.setOnKeyListener((v, keyCode, event) -> {
            //修改回车键功能
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                mSearchAdapter.setHolderType(true);
                saveKey();
                return true;
            }
            return false;
        });

        mHotAdapter.setOnItemClickListener((view, pos) -> {
            mSearchAdapter.setHolderType(true);
            refreshLayout.setVisibility(View.VISIBLE);
            mTvSearch.setText(mHotList.get(pos));
            saveKey();
        });

        mHisAdapter.setOnItemClickListener((view, pos) -> {
            mSearchAdapter.setHolderType(true);
            refreshLayout.setVisibility(View.VISIBLE);
            mTvSearch.setText(mHisList.get(pos).getKey());
            saveKey();
        });

        mSearchAdapter.setOnItemClickListener((view, pos) -> {
            mSearchAdapter.setHolderType(true);
            mTvSearch.setText(mSearchList.get(pos).getTitle());
            saveKey();
        });
        headHistory.setOnClickListener(view -> DialogUtils.getInstance()
                .showAlertDialog(NovelSearchActivity.this,
                        getString(R.string.clear_search), (dialogInterface, i) -> {
            LitePal.deleteAll(SearchListTable.class);
            mHisList.clear();
            mHisList.addAll(LitePal.order("saveTime desc").limit(5).find(SearchListTable.class));
            mHisAdapter.notifyDataSetChanged();
        }));

        refreshLayout.setOnReloadingListener(new RefreshLayout.OnReloadingListener() {
            @Override
            public void onReload() {
                getData();
            }
        });

    }

    private void saveKey() {
        if (mTvSearch.getText().toString().trim().equals("")) {
            return;
        }
        SearchListTable searchListTable = new SearchListTable();
        searchListTable.setKey(mTvSearch.getText().toString().trim());
        searchListTable.setSaveTime(System.currentTimeMillis());
        searchListTable.saveOrUpdate("key=?", mTvSearch.getText().toString().trim());
        mHisList.clear();
        mHisList.addAll(LitePal.order("saveTime desc").limit(5).find(SearchListTable.class));
        mHisAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (refreshLayout.getVisibility() == View.VISIBLE) {
            mTvSearch.setText("");
            mSearchAdapter.setHolderType(false);
            page = 1;
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.message_fade_in, R.anim.message_fade_out);
        }
    }

    @OnClick(R.id.tv_cancel)
    public void onViewClicked() {
        onBackPressed();
    }

    @Subscribe
    public void getHotSearch(HotSearchEvent event) {

        if (event.isFail()) {

        } else {
            mHotList.clear();
            mHotList.addAll(event.getResult().getKey());
            mHotAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void getSearchList(SearchListEvent event) {
        refreshLayout.showFinish();
        if (event.isFail()) {
            refreshLayout.showError();
        } else {
            loadSize = event.getResult().getBook().size();
            if (mSearchAdapter.isLoadingMore()){
                mSearchList.remove(mSearchList.size() - 1);
                mSearchList.addAll(event.getResult().getBook());
                mSearchAdapter.notifyDataSetChanged();
                mSearchAdapter.setLoadingMore(false);
            }else {
                mSearchList.clear();
                mSearchList.addAll(event.getResult().getBook());
                mSearchAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
    }
}
