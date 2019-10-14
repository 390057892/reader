package com.novel.read.activity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.adapter.BookListAdapter;
import com.novel.read.base.NovelBaseActivity;
import com.novel.read.constants.Constant;
import com.novel.read.event.SearchListEvent;
import com.novel.read.http.AccountManager;
import com.novel.read.model.protocol.SearchResp;
import com.novel.read.widget.RefreshLayout;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.novel.read.constants.Constant.COMMENT_SIZE;

public class NovelBookTypeListActivity extends NovelBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rlv_type_list)
    RecyclerView mRlvTypeList;
    @BindView(R.id.refresh)
    RefreshLayout refreshLayout;
    private List<SearchResp.BookBean> mList;
    private BookListAdapter mAdapter;
    private String mCategoryId;
    private String mTitle;
    private int page = 1;
    private int loadSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_book_type_list;
    }

    @Override
    protected void initView() {
        EventManager.Companion.getInstance().registerSubscriber(this);

        mCategoryId = getIntent().getStringExtra(Constant.Bundle.CategoryId);
        mTitle = getIntent().getStringExtra(Constant.Bundle.mTitle);

        mRlvTypeList.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();

        mAdapter = new BookListAdapter(mList, mRlvTypeList);
        mRlvTypeList.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(() -> {
            if (mAdapter.isLoadingMore()) {

            } else {
                if (loadSize >= COMMENT_SIZE) {
                    mAdapter.setLoadingMore(true);
                    mList.add(null);
                    mAdapter.notifyDataSetChanged();
                    page++;
                    getData();
                }
            }
        });
    }


    @Override
    protected void initData() {
        refreshLayout.showLoading();
        refreshLayout.setOnReloadingListener(this::getData);
        getData();
        toolbar.setTitle(mTitle);
        toolbar.setNavigationOnClickListener(view -> finish());

    }

    private void getData() {
        AccountManager.getInstance().getSearchBookList(mCategoryId, "", page);
    }

    @Subscribe
    public void getSearchList(SearchListEvent event) {
        refreshLayout.showFinish();
        if (event.isFail()) {
            refreshLayout.showError();
        } else {
            loadSize = event.getResult().getBook().size();
            if (mAdapter.isLoadingMore()){
                mList.remove(mList.size() - 1);
                mList.addAll(event.getResult().getBook());
                mAdapter.notifyDataSetChanged();
                mAdapter.setLoadingMore(false);
            }else {
                mList.clear();
                mList.addAll(event.getResult().getBook());
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
    }
}
