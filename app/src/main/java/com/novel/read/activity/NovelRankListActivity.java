package com.novel.read.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.read.R;
import com.novel.read.adapter.RankListAdapter;
import com.novel.read.base.NovelBaseActivity;
import com.novel.read.constants.Constant;
import com.novel.read.http.AccountManager;
import com.novel.read.model.protocol.RankByUpadateResp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.novel.read.constants.Constant.COMMENT_SIZE;

/**
 * 推荐fragment中点击更多跳转来的。
 */
public class NovelRankListActivity extends NovelBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rlv_book_list)
    RecyclerView mRlvBookList;
    private RankListAdapter mAdapter;
    private List<RankByUpadateResp.BookBean> mList;
    private int page = 1;
    private int loadSize;
    private String type;
    private String sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rank_list;
    }

    @Override
    protected void initView() {
        mList = new ArrayList<>();

        mRlvBookList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RankListAdapter(mList, mRlvBookList);
        mRlvBookList.setAdapter(mAdapter);
        sex = getIntent().getStringExtra(Constant.Sex);
        type = getIntent().getStringExtra(Constant.Type);
        switch (type) {
            case Constant.ListType.Human:
                toolbar.setTitle(getString(R.string.popular_selection));
                break;
            case Constant.ListType.EditRecommend:
                toolbar.setTitle(getString(R.string.edit_recommend));
                break;
            case Constant.ListType.HotSearch:
                toolbar.setTitle(getString(R.string.hot_search));
                break;
        }
        getData();
    }

    @Override
    protected void initData() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

    private void getData() {
        AccountManager.getInstance().getRankList(type, sex, Constant.DateTyp.Week, String.valueOf(page), new RankCallBack());
    }

    private class RankCallBack implements Callback<RankByUpadateResp> {

        @Override
        public void onResponse(@NonNull Call<RankByUpadateResp> call, @NonNull Response<RankByUpadateResp> response) {
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    loadSize = response.body().getBook().size();
                    if (mAdapter.isLoadingMore()) {
                        mList.remove(mList.size() - 1);
                        mList.addAll(response.body().getBook());
                        mAdapter.notifyDataSetChanged();
                        mAdapter.setLoadingMore(false);
                    } else {
                        mList.clear();
                        mList.addAll(response.body().getBook());
                        mAdapter.notifyDataSetChanged();
                    }

                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<RankByUpadateResp> call, @NonNull Throwable t) {

        }
    }
}
