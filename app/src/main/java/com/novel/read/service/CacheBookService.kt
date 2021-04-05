package com.novel.read.service

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.BaseService
import com.novel.read.constant.AppConst
import com.novel.read.constant.EventBus
import com.novel.read.constant.IntentAction
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.help.AppConfig
import com.novel.read.help.BookHelp
import com.novel.read.help.IntentHelp
import com.novel.read.service.help.CacheBook
import com.novel.read.utils.ext.postEvent
import com.novel.read.help.coroutine.CompositeCoroutine
import com.novel.read.help.coroutine.Coroutine
import com.novel.read.network.repository.BookRepository
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.isActive
import org.jetbrains.anko.toast
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors

class CacheBookService : BaseService() {
    private val threadCount = AppConfig.threadCount
    private var searchPool =
        Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()
    private var tasks = CompositeCoroutine()
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable = Runnable { upDownload() }
    private val bookMap = ConcurrentHashMap<Long, Book>()
    private val downloadMap = ConcurrentHashMap<Long, CopyOnWriteArraySet<BookChapter>>()
    private val downloadCount = ConcurrentHashMap<Long, DownloadCount>()
    private val finalMap = ConcurrentHashMap<Long, CopyOnWriteArraySet<BookChapter>>()
    private val downloadingList = CopyOnWriteArraySet<Long>()
    private val bookRepository by lazy { BookRepository() }

    @Volatile
    private var downloadingCount = 0
    private var notificationContent = App.INSTANCE.getString(R.string.starting_download)

    private val notificationBuilder by lazy {
        val builder = NotificationCompat.Builder(this, AppConst.channelIdDownload)
            .setSmallIcon(R.drawable.ic_download)
            .setOngoing(true)
            .setContentTitle(getString(R.string.offline_cache))
        builder.addAction(
            R.drawable.ic_stop_black_24dp,
            getString(R.string.cancel),
            IntentHelp.servicePendingIntent<CacheBookService>(this, IntentAction.stop)
        )
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }

    override fun onCreate() {
        super.onCreate()
        updateNotification(notificationContent)
        handler.postDelayed(runnable, 1000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                IntentAction.start -> addDownloadData(
                    intent.getLongExtra("bookId", 0),
                    intent.getIntExtra("start", 0),
                    intent.getIntExtra("end", 0)
                )
                IntentAction.remove -> removeDownload(intent.getLongExtra("bookId", 0))
                IntentAction.stop -> stopDownload()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        tasks.clear()
        searchPool.close()
        handler.removeCallbacks(runnable)
        downloadMap.clear()
        finalMap.clear()
        super.onDestroy()
        postEvent(EventBus.UP_DOWNLOAD, downloadMap)
    }

    private fun getBook(bookId: Long): Book? {
        var book = bookMap[bookId]
        if (book == null) {
            synchronized(this) {
                book = bookMap[bookId]
                if (book == null) {
                    book = App.db.getBookDao().getBook(bookId.toString())
                    if (book == null) {
                        removeDownload(bookId)
                    }
                }
            }
        }
        return book
    }

    private fun addDownloadData(bookId: Long, start: Int, end: Int) {
        bookId ?: return
        if (downloadMap.containsKey(bookId)) {
            updateNotification(getString(R.string.already_in_download))
            toast(R.string.already_in_download)
            return
        }
        downloadCount[bookId] = DownloadCount()
        execute {
            App.db.getChapterDao().getChapterList(bookId, start, end).let {
                if (it.isNotEmpty()) {
                    val chapters = CopyOnWriteArraySet<BookChapter>()
                    chapters.addAll(it)
                    downloadMap[bookId] = chapters
                } else {
                    CacheBook.addLog("${getBook(bookId)?.bookName} is empty")
                }
            }
            for (i in 0 until threadCount) {
                if (downloadingCount < threadCount) {
                    download()
                }
            }
        }
    }

    private fun removeDownload(bookId: Long?) {
        downloadMap.remove(bookId)
        finalMap.remove(bookId)
    }

    private fun download() {
        downloadingCount += 1
        val task = Coroutine.async(this, context = searchPool) {
            if (!isActive) return@async
            val bookChapter: BookChapter? = synchronized(this@CacheBookService) {
                downloadMap.forEach {
                    it.value.forEach { chapter ->
                        if (!downloadingList.contains(chapter.chapterId)) {
                            downloadingList.add(chapter.chapterId)
                            return@synchronized chapter
                        }
                    }
                }
                return@synchronized null
            }
            if (bookChapter == null) {
                postDownloading(false)
            } else {
                val book = getBook(bookChapter.bookId)
                if (book == null) {
                    postDownloading(true)
                    return@async
                }
                if (!BookHelp.hasContent(book, bookChapter)) {

                    bookRepository.getContents(this, book, bookChapter)
                        .timeout(60000L)
                        .onSuccess {
                            val content = it.chapter.chapterContent
                            if (content.isNotBlank()) {
                                BookHelp.saveContent(book, bookChapter, content)
                            }
                            synchronized(this@CacheBookService) {
                                downloadCount[book.bookId]?.increaseSuccess()
                                downloadCount[book.bookId]?.increaseFinished()
                                downloadCount[book.bookId]?.let {
                                    updateNotification(
                                        it,
                                        downloadMap[book.bookId]?.size,
                                        bookChapter.chapterName
                                    )
                                }
                                val chapterMap =
                                    finalMap[book.bookId]
                                        ?: CopyOnWriteArraySet<BookChapter>().apply {
                                            finalMap[book.bookId] = this
                                        }
                                chapterMap.add(bookChapter)
                                if (chapterMap.size == downloadMap[book.bookId]?.size) {
                                    downloadMap.remove(book.bookId)
                                    finalMap.remove(book.bookId)
                                    downloadCount.remove(book.bookId)
                                }
                            }
                        }.onError {
                            synchronized(this) {
                                downloadingList.remove(bookChapter.chapterId)
                            }
                            notificationContent = "getContentError${it.localizedMessage}"
                            upNotification()
                        }.onFinally {
                            postDownloading(true)
                        }
                } else {
                    //无需下载的，设置为增加成功
                    downloadCount[book.bookId]?.increaseSuccess()
                    downloadCount[book.bookId]?.increaseFinished()
                    postDownloading(true)
                }
            }
        }.onError {
            CacheBook.addLog("ERROR:${it.localizedMessage}")
            updateNotification("ERROR:${it.localizedMessage}")
        }
        tasks.add(task)
    }

    /**
     * 更新通知
     */
    private fun upNotification() {
        notificationBuilder.setContentText(notificationContent)
        val notification = notificationBuilder.build()
        startForeground(AppConst.notificationIdDownload, notification)
    }

    private fun postDownloading(hasChapter: Boolean) {
        downloadingCount -= 1
        if (hasChapter) {
            download()
        } else {
            if (downloadingCount < 1) {
                stopDownload()
            }
        }
    }

    private fun stopDownload() {
        tasks.clear()
        stopSelf()
    }

    private fun upDownload() {
        updateNotification(notificationContent)
        postEvent(EventBus.UP_DOWNLOAD, downloadMap)
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 1000)
    }

    private fun updateNotification(
        downloadCount: DownloadCount,
        totalCount: Int?,
        content: String
    ) {
        notificationContent =
            "进度:" + downloadCount.downloadFinishedCount + "/" + totalCount + ",成功:" + downloadCount.successCount + "," + content
    }

    /**
     * 更新通知
     */
    private fun updateNotification(content: String) {
        notificationBuilder.setContentText(content)
        val notification = notificationBuilder.build()
        startForeground(AppConst.notificationIdDownload, notification)
    }

    class DownloadCount {
        @Volatile
        var downloadFinishedCount = 0 // 下载完成的条目数量

        @Volatile
        var successCount = 0 //下载成功的条目数量

        fun increaseSuccess() {
            ++successCount
        }

        fun increaseFinished() {
            ++downloadFinishedCount
        }
    }
}