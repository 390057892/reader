package com.novel.read.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.read.R;
import com.novel.read.adapter.RankListAdapter;
import com.novel.read.base.NovelBaseFragment;
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


public class BookListFragment extends NovelBaseFragment {

    @BindView(R.id.rlv_book_list)
    RecyclerView mRlvBookList;
    private RankListAdapter mAdapter;
    private List<RankByUpadateResp.BookBean> mList;
    String sex;
    String dateType;
    String type;
    private int page = 1;
    private int loadSize;

    public static BookListFragment newInstance(String type, String dateType, String sex) {
        Bundle args = new Bundle();
        args.putString(Constant.Sex, sex);
        args.putString(Constant.DateType, dateType);
        args.putString(Constant.Type, type);
        BookListFragment fragment = new BookListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_book_list;
    }

    @Override
    protected void initView() {
        mList = new ArrayList<>();

        mRlvBookList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RankListAdapter(mList,mRlvBookList);
        mRlvBookList.setAdapter(mAdapter);

        if (getArguments() != null) {
            sex = getArguments().getString(Constant.Sex);
            dateType = getArguments().getString(Constant.DateType);
            type = getArguments().getString(Constant.Type);
        }

    }

    @Override
    protected void initData() {
        getData();
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
        AccountManager.getInstance().getRankList(type, sex, dateType, String.valueOf(page), new RankCallBack());
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
                    }else {
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
