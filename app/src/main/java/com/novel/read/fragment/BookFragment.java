package com.novel.read.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mango.mangolib.event.EventManager;
import com.novel.read.R;
import com.novel.read.activity.NovelMainActivity;
import com.novel.read.activity.NovelSearchActivity;
import com.novel.read.adapter.BookAdapter;
import com.novel.read.base.NovelBaseFragment;
import com.novel.read.constants.Constant;
import com.novel.read.event.HideBottomBarEvent;
import com.novel.read.event.SwitchFragmentEvent;
import com.novel.read.event.UpdateBookEvent;
import com.novel.read.http.AccountManager;
import com.novel.read.model.db.BookRecordBean;
import com.novel.read.model.db.CollBookBean;
import com.novel.read.model.db.dbManage.BookRepository;
import com.novel.read.model.protocol.BookDetailResp;
import com.novel.read.utlis.LocalManageUtil;
import com.novel.read.utlis.RxUtils;
import com.novel.read.utlis.SpUtil;
import com.novel.read.utlis.ToastUtil;
import com.squareup.otto.Subscribe;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * create by 赵利君 on 2019/10/14
 * describe:
 */
public class BookFragment extends NovelBaseFragment {

    @BindView(R.id.title)
    Toolbar title;
    @BindView(R.id.title_edit)
    Toolbar titleEdit;
    @BindView(R.id.rlv_book)
    RecyclerView mRlvBook;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.tv_delete)
    TextView mTvDelete;
    private BookAdapter mAdapter;
    private List<CollBookBean> mList = new ArrayList<>();
    private boolean isInit = true;

    public static BookFragment newInstance() {
        Bundle args = new Bundle();
        BookFragment fragment = new BookFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_book;
    }

    @Override
    protected void initView() {
        EventManager.Companion.getInstance().registerSubscriber(this);

        mList.addAll(BookRepository.getInstance().getCollBooks());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if (mList == null || mList.size() == 0) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });

        mRlvBook.setLayoutManager(gridLayoutManager);
        mAdapter = new BookAdapter(mList);
        mRlvBook.setAdapter(mAdapter);
        if (isInit) {
            isInit = false;
            update(mList);
        }
    }

    @Override
    protected void initData() {
        title.inflateMenu(R.menu.title_book);
        titleEdit.inflateMenu(R.menu.title_edit);
        setOnClick();
    }


    private void setOnClick() {
        title.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_search:
                    toActivity(NovelSearchActivity.class);
                    getActivity().overridePendingTransition(R.anim.message_fade_in, R.anim.message_fade_out);
                    break;
                case R.id.edit_book:
                    if (mList == null || mList.size() == 0) { //没书的时候提醒用户不能编辑
                        ToastUtil.show(getActivity(), getString(R.string.please_add_book));
                    } else {
                        mAdapter.setEdit(true);
                        mTvCancel.setVisibility(View.VISIBLE);
                        mTvDelete.setVisibility(View.VISIBLE);
                        titleEdit.setVisibility(View.VISIBLE);
                        title.setVisibility(View.GONE);
                        EventManager.Companion.getInstance().postEvent(new HideBottomBarEvent(true));
                    }
                    break;
                case R.id.book_sort:
                    showBookSortDialog();
                    break;
                case R.id.menu2:
                    showLanguageDialog();
                    break;
                case R.id.menu3:
                    if (SpUtil.getBooleanValue(Constant.NIGHT)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        SpUtil.setBooleanValue(Constant.NIGHT, false);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        SpUtil.setBooleanValue(Constant.NIGHT, true);
                    }
                    getActivity().recreate();
                    break;
            }
            return true;
        });
        titleEdit.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.action_edit) {
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setSelect(true);
                }
                mAdapter.notifyDataSetChanged();
            }
            return true;
        });
        mTvCancel.setOnClickListener(view -> {
            EventManager.Companion.getInstance().postEvent(new HideBottomBarEvent(false));
            updateBook(new UpdateBookEvent());
        });

        mTvDelete.setOnClickListener(view -> {
            List<CollBookBean> deleteList = mAdapter.getSelectList();
            for (int i = 0; i < deleteList.size(); i++) {
                if (deleteList.get(i).isSaved()) {
                    int count = deleteList.get(i).delete();
                    LitePal.deleteAll(BookRecordBean.class, "bookId=?", mList.get(i).getId());
                    Log.e("count", "setOnClick: " + count);
                }
            }
            ToastUtil.show(getActivity(), getString(R.string.delete_success));
            EventManager.Companion.getInstance().postEvent(new HideBottomBarEvent(false));
            updateBook(new UpdateBookEvent());
        });

        mAdapter.setOnItemClickListener((view, pos) -> {
            EventManager.Companion.getInstance().postEvent(new SwitchFragmentEvent());
        });

    }


    private void showLanguageDialog() {
        Log.e("showLanguageDialog", "showLanguageDialog: " + SpUtil.getIntValue(Constant.Language, 0));
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.choose_language))
                .setSingleChoiceItems(getResources().getStringArray(R.array.setting_dialog_language_choice), SpUtil.getIntValue(Constant.Language, 1),
                        (dialog, which) -> {
                            String language = getResources().getStringArray(R.array.setting_dialog_language_choice)[which];
                            SpUtil.setIntValue(Constant.Language, which);
                            dialog.dismiss();

                            if (which == 0) {
                                selectLanguage(0);
                            } else {
                                selectLanguage(1);
                            }
                        })
                .create().show();
    }

    private void selectLanguage(int select) {
        LocalManageUtil.saveSelectLanguage(getActivity(), select);
        NovelMainActivity.reStart(getActivity());
    }

    private void showBookSortDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.choose_language))
                .setSingleChoiceItems(getResources().getStringArray(R.array.setting_dialog_sort_choice),
                        SpUtil.getBooleanValue(Constant.BookSort, false) ? 0 : 1,
                        (dialog, which) -> {
                            if (which == 0) {
                                SpUtil.setBooleanValue(Constant.BookSort, true);
                            } else {
                                SpUtil.setBooleanValue(Constant.BookSort, false);
                            }
                            updateBook(new UpdateBookEvent());
                            dialog.dismiss();
                        })
                .create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBook(new UpdateBookEvent());
    }

    @Subscribe
    public void updateBook(UpdateBookEvent event) {
        mTvCancel.setVisibility(View.GONE);
        mTvDelete.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        titleEdit.setVisibility(View.GONE);
        mList.clear();
        mList.addAll(BookRepository.getInstance().getCollBooks());
        mAdapter.setEdit(false);
    }

    private void update(List<CollBookBean> collBookBeans) { //检测书籍更新
        if (collBookBeans == null || collBookBeans.isEmpty()) return;
        List<CollBookBean> collBooks = new ArrayList<>(collBookBeans);
        List<Single<BookDetailResp>> observables = new ArrayList<>(collBooks.size());
        Iterator<CollBookBean> it = collBooks.iterator();
        while (it.hasNext()) {
            CollBookBean collBook = it.next();
            //删除本地文件
            if (collBook.isLocal()) {
                it.remove();
            } else {
                observables.add(AccountManager.getInstance().getBookDetails(collBook.getId()));
            }
        }
        //zip可能不是一个好方法。
        Single.zip(observables, objects -> {
            List<CollBookBean> newCollBooks = new ArrayList<>(objects.length);
            for (int i = 0; i < collBooks.size(); ++i) {
                CollBookBean oldCollBook = collBooks.get(i);
                CollBookBean newCollBook = ((BookDetailResp) objects[i]).getCollBookBean();
                //如果是oldBook是update状态，或者newCollBook与oldBook章节数不同
                if (oldCollBook.isUpdate() || !oldCollBook.getLastChapter().equals(newCollBook.getLastChapter())) {
                    newCollBook.setIsUpdate(true);
                } else {
                    newCollBook.setIsUpdate(false);
                }
                newCollBook.setLastRead(oldCollBook.getLastRead());
                newCollBooks.add(newCollBook);
                //存储到数据库中
                BookRepository.getInstance().saveCollBooks(newCollBooks);
            }
            return newCollBooks;
        })
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new SingleObserver<List<CollBookBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        addDisposable(d);
                    }

                    @Override
                    public void onSuccess(List<CollBookBean> value) {
                        //跟原先比较
                        mList.clear();
                        mList.addAll(BookRepository.getInstance().getCollBooks());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.Companion.getInstance().unregisterSubscriber(this);
    }
}