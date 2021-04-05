package com.novel.read.ui.main.bookshelf.arrange

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.PreferKey
import com.novel.read.data.db.entity.Book
import com.novel.read.lib.ATH
import com.novel.read.lib.dialogs.alert
import com.novel.read.lib.dialogs.okButton
import com.novel.read.ui.widget.VerticalDivider
import com.novel.read.utils.ext.applyTint
import com.novel.read.utils.ext.getPrefInt
import com.novel.read.utils.ext.getViewModel
import kotlinx.android.synthetic.main.activity_arrange_book.*

class ArrangeBookActivity : VMBaseActivity<ArrangeBookViewModel>(R.layout.activity_arrange_book),ArrangeBookAdapter.CallBack {

    private lateinit var adapter: ArrangeBookAdapter

    override val viewModel: ArrangeBookViewModel
        get() = getViewModel(ArrangeBookViewModel::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initView()
        initBookData()
    }

    private fun initView() {
        ATH.applyEdgeEffectColor(recycler_view)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addItemDecoration(VerticalDivider(this))
        adapter = ArrangeBookAdapter(this)
        recycler_view.adapter = adapter
    }

    private fun initBookData() {
        viewModel.booksLiveData.removeObservers(this)
        viewModel.booksLiveData.value = App.db.getBookDao().getAllBooks()

        viewModel.booksLiveData.observe(this, { list ->
            Log.e("ArrangeBookActivity", "observeLiveBus: 开始更新")
            adapter.isUseEmpty = list.isEmpty()
            val books = when (getPrefInt(PreferKey.bookshelfSort)) {
                1 -> list.sortedByDescending { it.lastUpdateChapterDate }
                2 -> list.sortedBy { it.bookName }
                else -> list.sortedByDescending { it.durChapterTime }
            }

            adapter.setList(books.toMutableList())
        })
    }

    override fun upSelectCount() {
    }

    override fun updateBook(vararg book: Book) {
    }

    override fun deleteBook(book: Book) {
        alert(titleResource = R.string.draw, messageResource = R.string.sure_del) {
            okButton {
                viewModel.deleteBook(book)
            }
        }.show().applyTint()
    }

}