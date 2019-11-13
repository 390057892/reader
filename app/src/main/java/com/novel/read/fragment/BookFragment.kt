package com.novel.read.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.activity.NovelMainActivity
import com.novel.read.activity.NovelSearchActivity
import com.novel.read.adapter.BookAdapter
import com.novel.read.base.NovelBaseFragment
import com.novel.read.constants.Constant
import com.novel.read.event.HideBottomBarEvent
import com.novel.read.event.SwitchFragmentEvent
import com.novel.read.event.UpdateBookEvent
import com.novel.read.http.AccountManager
import com.novel.read.model.db.BookRecordBean
import com.novel.read.model.db.CollBookBean
import com.novel.read.model.db.dbManage.BookRepository
import com.novel.read.model.protocol.BookDetailResp
import com.novel.read.showToast
import com.novel.read.utlis.LocalManageUtil
import com.novel.read.utlis.RxUtils
import com.novel.read.utlis.SpUtil
import com.squareup.otto.Subscribe
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_book.*
import org.litepal.LitePal
import java.util.*

/**
 * create by 赵利君 on 2019/10/14
 * describe:
 */
class BookFragment : NovelBaseFragment() {

    private lateinit var mAdapter: BookAdapter
    private val mList = ArrayList<CollBookBean>()
    private var isInit = true

    override fun getLayoutId(): Int {
        return R.layout.fragment_book
    }

    override fun initView() {
        EventManager.instance.registerSubscriber(this)

        mList.addAll(BookRepository.getInstance().collBooks)
        val gridLayoutManager = GridLayoutManager(activity, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return if (mList.size == 0) {
                    3
                } else {
                    1
                }
            }
        }

        rlv_book.layoutManager = gridLayoutManager
        mAdapter = BookAdapter(mList)
        rlv_book.adapter = mAdapter
        if (isInit) {
            isInit = false
            update(mList)
        }
    }

    override fun initData() {
        title.inflateMenu(R.menu.title_book)
        title_edit.inflateMenu(R.menu.title_edit)
        setOnClick()
    }

    private fun setOnClick() {
        title.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> {
                    toActivity(NovelSearchActivity::class.java)
                    activity!!.overridePendingTransition(
                        R.anim.message_fade_in,
                        R.anim.message_fade_out
                    )
                }
                R.id.edit_book -> if (mList.size == 0) { //没书的时候提醒用户不能编辑
                    activity!!.showToast(getString(R.string.please_add_book))
                } else {
                    mAdapter.setEdit(true)
                    tv_cancel.visibility = View.VISIBLE
                    tv_delete.visibility = View.VISIBLE
                    title_edit.visibility = View.VISIBLE
                    title.visibility = View.GONE
                    EventManager.instance.postEvent(HideBottomBarEvent(true))
                }
                R.id.book_sort -> showBookSortDialog()
                R.id.menu2 -> showLanguageDialog()
                R.id.menu3 -> {
                    if (SpUtil.getBooleanValue(Constant.NIGHT)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        SpUtil.setBooleanValue(Constant.NIGHT, false)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        SpUtil.setBooleanValue(Constant.NIGHT, true)
                    }
                    activity!!.recreate()
                }
            }
            true
        }
        title_edit.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_edit) {
                for (i in mList.indices) {
                    mList[i].isSelect = true
                }
                mAdapter.notifyDataSetChanged()
            }
            true
        }
        tv_cancel.setOnClickListener { view ->
            EventManager.instance.postEvent(HideBottomBarEvent(false))
            updateBook(UpdateBookEvent())
        }

        tv_delete.setOnClickListener { view ->
            val deleteList = mAdapter.selectList
            for (i in deleteList.indices) {
                if (deleteList[i].isSaved) {
                    val count = deleteList[i].delete()
                    LitePal.deleteAll(BookRecordBean::class.java, "bookId=?", mList[i].id)
                    Log.e("count", "setOnClick: $count")
                }
            }
            activity!!.showToast( getString(R.string.delete_success))
            EventManager.instance.postEvent(HideBottomBarEvent(false))
            updateBook(UpdateBookEvent())
        }

        mAdapter.setOnItemClickListener { view, pos ->
            EventManager.instance.postEvent(
                SwitchFragmentEvent()
            )
        }

    }


    private fun showLanguageDialog() {
        AlertDialog.Builder(activity)
            .setTitle(getString(R.string.choose_language))
            .setSingleChoiceItems(
                resources.getStringArray(R.array.setting_dialog_language_choice),
                SpUtil.getIntValue(Constant.Language, 1)
            ) { dialog, which ->
                resources.getStringArray(R.array.setting_dialog_language_choice)[which]
                SpUtil.setIntValue(Constant.Language, which)
                dialog.dismiss()
                if (which == 0) {
                    selectLanguage(0)
                } else {
                    selectLanguage(1)
                }
            }.create().show()
    }

    private fun selectLanguage(select: Int) {
        LocalManageUtil.saveSelectLanguage(activity, select)
        NovelMainActivity.reStart(activity!!)
    }

    private fun showBookSortDialog() {
        AlertDialog.Builder(activity)
            .setTitle(getString(R.string.choose_language))
            .setSingleChoiceItems(
                resources.getStringArray(R.array.setting_dialog_sort_choice),
                if (SpUtil.getBooleanValue(Constant.BookSort, false)) 0 else 1
            ) { dialog, which ->
                if (which == 0) {
                    SpUtil.setBooleanValue(Constant.BookSort, true)
                } else {
                    SpUtil.setBooleanValue(Constant.BookSort, false)
                }
                updateBook(UpdateBookEvent())
                dialog.dismiss()
            }
            .create().show()
    }

    override fun onResume() {
        super.onResume()
        updateBook(UpdateBookEvent())
    }

    @Subscribe
    fun updateBook(event: UpdateBookEvent) {
        tv_cancel.visibility = View.GONE
        tv_delete.visibility = View.GONE
        title.visibility = View.VISIBLE
        title_edit.visibility = View.GONE
        mList.clear()
        mList.addAll(BookRepository.getInstance().collBooks)
        mAdapter.setEdit(false)
    }

    private fun update(collBookBeans: List<CollBookBean>?) { //检测书籍更新
        if (collBookBeans == null || collBookBeans.isEmpty()) return
        val collBooks = ArrayList(collBookBeans)
        val observables = ArrayList<Single<BookDetailResp>>(collBooks.size)
        val it = collBooks.iterator()
        while (it.hasNext()) {
            val collBook = it.next()
            //删除本地文件
            if (collBook.isLocal) {
                it.remove()
            } else {
                observables.add(AccountManager.getInstance().getBookDetails(collBook.id))
            }
        }
        //zip可能不是一个好方法。
        Single.zip<BookDetailResp, List<CollBookBean>>(observables) { objects ->
            val newCollBooks = ArrayList<CollBookBean>(objects.size)
            for (i in collBooks.indices) {
                val oldCollBook = collBooks[i]
                val newCollBook = (objects[i] as BookDetailResp).collBookBean
                //如果是oldBook是update状态，或者newCollBook与oldBook章节数不同
                if (oldCollBook.isUpdate || oldCollBook.lastChapter != newCollBook.lastChapter) {
                    newCollBook.setIsUpdate(true)
                } else {
                    newCollBook.setIsUpdate(false)
                }
                newCollBook.lastRead = oldCollBook.lastRead
                newCollBooks.add(newCollBook)
                //存储到数据库中
                BookRepository.getInstance().saveCollBooks(newCollBooks)
            }
            newCollBooks
        }.compose<List<CollBookBean>> {
            RxUtils.toSimpleSingle(
                it
            )
        }.subscribe(object : SingleObserver<List<CollBookBean>> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(value: List<CollBookBean>) {
                //跟原先比较
                mList.clear()
                mList.addAll(BookRepository.getInstance().collBooks)
                mAdapter.notifyDataSetChanged()
            }

            override fun onError(e: Throwable) {}
        }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.instance.unregisterSubscriber(this)
    }

    companion object {

        fun newInstance(): BookFragment {
            val args = Bundle()
            val fragment = BookFragment()
            fragment.arguments = args
            return fragment
        }
    }
}