package com.novel.read.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.adapter.LoveLyAdapter
import com.novel.read.base.NovelBaseActivity
import com.novel.read.constants.Constant
import com.novel.read.constants.Constant.RequestCode.Companion.REQUEST_READ
import com.novel.read.constants.Constant.ResultCode.Companion.RESULT_IS_COLLECTED
import com.novel.read.event.BookArticleEvent
import com.novel.read.event.GetBookDetailEvent
import com.novel.read.event.GetRecommendBookEvent
import com.novel.read.event.UpdateBookEvent
import com.novel.read.http.AccountManager
import com.novel.read.model.db.CollBookBean
import com.novel.read.model.db.dbManage.BookRepository
import com.novel.read.model.protocol.RecommendBookResp
import com.novel.read.showToast
import com.novel.read.utlis.DateUtli
import com.novel.read.utlis.GlideImageLoader
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_book_detail.*
import org.litepal.LitePal
import java.util.*

class NovelBookDetailActivity : NovelBaseActivity(), View.OnClickListener {

    private lateinit var mAdapter: LoveLyAdapter
    private val mList = ArrayList<RecommendBookResp.BookBean>()

    private var mBookId: Int = 0
    private var isCollected = false
    private var mCollBookBean: CollBookBean? = null

    private var mProgressDialog: ProgressDialog? = null

    override val layoutId: Int get() = R.layout.activity_book_detail

    override fun initView() {
        mBookId = intent.getIntExtra(Constant.Bundle.BookId, 0)
        rlv_lovely.layoutManager = LinearLayoutManager(this)
        mAdapter = LoveLyAdapter(mList)
        rlv_lovely.adapter = mAdapter
    }

    override fun initData() {
        refresh.showLoading()
        refresh.setOnReloadingListener { getData() }
        getData()
        toolbar.setNavigationOnClickListener { finish() }
        tv_add_book.setOnClickListener(this)
        tv_start_read.setOnClickListener(this)
    }

    private fun getData() {
        AccountManager.getInstance().getRecommendBook(mBookId.toString(), "10")
        AccountManager.getInstance().getBookDetail(mBookId.toString())
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_add_book ->
                //点击存储
                if (isCollected) {
                    //放弃点击
                    BookRepository.getInstance().deleteCollBookInRx(mCollBookBean)
                    tv_add_book.text = resources.getString(R.string.add_book)
                    isCollected = false
                } else {
                    if (mProgressDialog == null) {
                        mProgressDialog = ProgressDialog(this)
                        mProgressDialog!!.setTitle("正在添加到书架中")
                    }
                    mProgressDialog!!.show()
                    AccountManager.getInstance()
                        .getBookArticle(mBookId.toString(), "2", "1", "100000")

                }
            R.id.tv_start_read ->
                startActivityForResult(
                    Intent(this, NovelReadActivity::class.java)
                        .putExtra(NovelReadActivity.EXTRA_IS_COLLECTED, isCollected)
                        .putExtra(NovelReadActivity.EXTRA_COLL_BOOK, mCollBookBean), REQUEST_READ
                )
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    fun getBookDetail(event: GetBookDetailEvent) {
        refresh.showFinish()
        if (event.isFail) {
            refresh.showError()
        } else {
            val bookBean = event.result!!.book
            GlideImageLoader.displayCornerImage(this, bookBean.cover, iv_book)
            tv_book_name.text = bookBean.title

            tv_book_author.text = bookBean.author + " | "
            tv_book_length.text = getString(R.string.book_word, bookBean.words / 10000)

            if (event.result!!.last_article != null) {
                tv_new_title.text =
                    getString(R.string.new_chapter, event.result!!.last_article.title)
                tv_update_time.text =
                    DateUtli.dateConvert(event.result!!.last_article.create_time, 0)
            }

            tv_human_num.text = bookBean.hot.toString() + ""
            tv_love_look_num.text = bookBean.like
            tv_Intro.text = bookBean.description
            mCollBookBean = BookRepository.getInstance().getCollBook(bookBean.id.toString())
            //判断是否收藏
            if (mCollBookBean != null) {
                isCollected = true
                tv_add_book.text = resources.getString(R.string.already_add)
                tv_start_read.text = resources.getString(R.string.go_read)
            } else {
                mCollBookBean = event.result!!.collBookBean
            }
        }
    }

    @Subscribe
    fun getRecommendBook(event: GetRecommendBookEvent) {
        if (event.isFail) {
            Log.e("getRecommendBook", event.er?.msg)
        } else {
            mList.clear()
            mList.addAll(event.result!!.book)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        EventManager.instance.registerSubscriber(this)
    }

    override fun onPause() {
        super.onPause()
        EventManager.instance.unregisterSubscriber(this)
    }

    @Subscribe
    fun getArticle(event: BookArticleEvent) {
        if (event.isFail) {
            dismiss()
            showToast(getString(R.string.net_error))
        } else {
            //存储收藏
            var success = false
            if (mCollBookBean != null) {
                success = mCollBookBean!!.saveOrUpdate("bookId=?", mCollBookBean!!.id)
            }
            if (success) {
                val bookChapterBean = event.result!!.chapterBean
                for (i in bookChapterBean.indices) {
                    bookChapterBean[i].collBookBean = mCollBookBean
                }
                LitePal.saveAllAsync(bookChapterBean).listen { success1 ->
                    if (success1) {
                        if (tv_add_book != null) {
                            tv_add_book.text = resources.getString(R.string.already_add)
                        }
                        isCollected = true
                    } else {
                        LitePal.deleteAll(CollBookBean::class.java, "bookId =?", mCollBookBean!!.id)
                        showToast(getString(R.string.net_error))
                    }
                    dismiss()
                }
            } else {
                showToast(getString(R.string.net_error))
                dismiss()
            }
        }

    }

    private fun dismiss() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //如果进入阅读页面收藏了，页面结束的时候，就需要返回改变收藏按钮
        if (requestCode == REQUEST_READ) {
            if (data == null) {
                return
            }

            isCollected = data.getBooleanExtra(RESULT_IS_COLLECTED, false)

            if (isCollected) {
                tv_add_book.text = resources.getString(R.string.already_add)
                tv_start_read.text = resources.getString(R.string.go_read)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.instance.postEvent(UpdateBookEvent())
    }
}
