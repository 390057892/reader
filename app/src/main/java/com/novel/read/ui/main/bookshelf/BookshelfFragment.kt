package com.novel.read.ui.main.bookshelf

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.VMBaseFragment
import com.novel.read.constant.EventBus
import com.novel.read.constant.IntentAction
import com.novel.read.constant.PreferKey
import com.novel.read.data.db.entity.Book
import com.novel.read.databinding.DialogBookshelfConfigBinding
import com.novel.read.databinding.FragmentBookShelfBinding
import com.novel.read.help.AppConfig
import com.novel.read.help.IntentDataHelp
import com.novel.read.lib.ATH
import com.novel.read.lib.dialogs.alert
import com.novel.read.lib.dialogs.noButton
import com.novel.read.lib.dialogs.okButton
import com.novel.read.ui.MainViewModel
import com.novel.read.ui.info.BookInfoActivity
import com.novel.read.ui.main.bookshelf.arrange.ArrangeBookActivity
import com.novel.read.ui.read.ReadBookActivity
import com.novel.read.ui.search.SearchActivity
import com.novel.read.utils.BooksDiffCallBack
import com.novel.read.utils.ext.*
import com.novel.read.utils.viewbindingdelegate.viewBinding
import org.jetbrains.anko.startActivity

class BookshelfFragment : VMBaseFragment<BookViewModel>(R.layout.fragment_book_shelf), BaseBookAdapter.CallBack  {

    override val viewModel: BookViewModel by viewModels()
    private val activityViewModel: MainViewModel
            by activityViewModels()
    private val binding by viewBinding(FragmentBookShelfBinding::bind)
    private lateinit var booksAdapter: BaseBookAdapter
    private var bookshelfLiveData = MutableLiveData<List<Book>>()
    private lateinit var selectBook: Book

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setSupportToolbar(binding.titleBar.toolbar)
        initRecycleView()
        upRecyclerData()
    }


    override fun onCompatCreateOptionsMenu(menu: Menu) {
        menuInflater.inflate(R.menu.main_bookshelf, menu)
    }

    override fun onCompatOptionsItemSelected(item: MenuItem) {
        super.onCompatOptionsItemSelected(item)
        when (item.itemId) {
            R.id.menu_search -> startActivity<SearchActivity>()
            R.id.menu_update_toc -> {
//                val group = bookGroups[tab_layout.selectedTabPosition]
//                val fragment = fragmentMap[group.groupId]
//                fragment?.getBooks()?.let {
//                    activityViewModel.upToc(it)
//                }
            }
            R.id.menu_bookshelf_layout -> configBookshelf()
            R.id.menu_arrange_bookshelf -> startActivity<ArrangeBookActivity>()
        }
    }

    private fun initRecycleView() {
        ATH.applyEdgeEffectColor(binding.rlvBookShelf)

        binding.refreshLayout.setColorSchemeColors(accentColor)
        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = false
            activityViewModel.upToc(booksAdapter.data)
        }

        val bookshelfLayout = getPrefInt(PreferKey.bookshelfLayout)
        if (bookshelfLayout == 0) {
            binding.rlvBookShelf.layoutManager = LinearLayoutManager(context)
            booksAdapter = BookShelfAdapter(this)
        } else {
            binding.rlvBookShelf.layoutManager = GridLayoutManager(context, bookshelfLayout + 2)
            booksAdapter = BooksAdapterGrid(this)
        }
        binding.rlvBookShelf.adapter = booksAdapter
        booksAdapter.setEmptyView(R.layout.view_empty)
        booksAdapter.setDiffCallback(BooksDiffCallBack())

        booksAdapter.setOnItemClickListener { adapter, view, position ->
            selectBook = adapter.data[position] as Book
            activity?.startActivity<ReadBookActivity>(
                Pair(IntentAction.bookId, selectBook.bookId),
                Pair("key", IntentDataHelp.putData(selectBook))
            )
//            ProcessLifecycleOwner.get().lifecycle.addObserver(App.ApplicationObserverInRead())
        }

        booksAdapter.setOnItemLongClickListener { adapter, view, position ->
            selectBook = adapter.data[position] as Book
            activity?.let { BookInfoActivity.actionBookInfo(it, selectBook.bookId, selectBook.bookTypeId) }
            true
        }
    }


    private fun upRecyclerData() {
        bookshelfLiveData.removeObservers(this)
        bookshelfLiveData.value = App.db.getBookDao().getAllBooks()

        bookshelfLiveData.observe(viewLifecycleOwner, { list ->
            Log.e("BookFragment", "observeLiveBus: 开始更新")
            booksAdapter.isUseEmpty = list.isEmpty()
            val books = when (getPrefInt(PreferKey.bookshelfSort)) {
                1 -> list.sortedByDescending { it.lastUpdateChapterDate }
                2 -> list.sortedBy { it.bookName }
                else -> list.sortedByDescending { it.durChapterTime }
            }

            booksAdapter.setList(books.toMutableList())
        })

    }

    fun gotoTop() {
        if (AppConfig.isEInkMode) {
            binding.rlvBookShelf.scrollToPosition(0)
        } else {
            binding.rlvBookShelf.smoothScrollToPosition(0)
        }
    }

    @SuppressLint("InflateParams")
    private fun configBookshelf() {
        requireContext().alert(titleResource = R.string.bookshelf_layout) {
            val bookshelfLayout = getPrefInt(PreferKey.bookshelfLayout)
            val bookshelfSort = getPrefInt(PreferKey.bookshelfSort)
            val alertBinding =
                DialogBookshelfConfigBinding.inflate(layoutInflater)
                    .apply {
                        rgLayout.checkByIndex(bookshelfLayout)
                        rgSort.checkByIndex(bookshelfSort)
                    }

            customView = alertBinding.root
            okButton {
                alertBinding.apply {
                    var changed = false
                    if (bookshelfLayout != rgLayout.getCheckedIndex()) {
                        putPrefInt(PreferKey.bookshelfLayout, rgLayout.getCheckedIndex())
                        changed = true
                    }
                    if (bookshelfSort != rgSort.getCheckedIndex()) {
                        putPrefInt(PreferKey.bookshelfSort, rgSort.getCheckedIndex())
                        changed = true
                    }
                    if (changed) {
                        activity?.recreate()
                    }
                }
            }
            noButton()
        }.show().applyTint()
    }

    override fun observeLiveBus() {
        super.observeLiveBus()
        observeEvent<Long>(EventBus.UP_BOOK) {
            bookshelfLiveData.value = App.db.getBookDao().getAllBooks()
        }
        observeEvent<Long>(EventBus.UPDATE_BOOK) {
            booksAdapter.notification(it)
        }
        observeEvent<String>(EventBus.SHOW_AD) {
            activity?.startActivity<ReadBookActivity>(
                Pair(IntentAction.bookId, selectBook.bookId),
                Pair("key", IntentDataHelp.putData(selectBook))
            )
        }

    }

    override fun isUpdate(bookId: Long): Boolean {
        return bookId in activityViewModel.updateList
    }
}