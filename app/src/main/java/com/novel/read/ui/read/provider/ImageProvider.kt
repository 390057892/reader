package io.legado.app.ui.book.read.page.provider

import android.graphics.Bitmap
import com.novel.read.data.db.entity.Book
import com.novel.read.help.BookHelp
import com.novel.read.utils.BitmapUtils
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

object ImageProvider {

    private val cache = ConcurrentHashMap<Int, ConcurrentHashMap<String, Bitmap>>()

    @Synchronized
    fun getCache(chapterIndex: Int, src: String): Bitmap? {
        return cache[chapterIndex]?.get(src)
    }

    @Synchronized
    fun setCache(chapterIndex: Int, src: String, bitmap: Bitmap) {
        var indexCache = cache[chapterIndex]
        if (indexCache == null) {
            indexCache = ConcurrentHashMap()
            cache[chapterIndex] = indexCache
        }
        indexCache[src] = bitmap
    }

    fun getImage(book: Book, chapterIndex: Int, src: String, onUi: Boolean = false): Bitmap? {
        getCache(chapterIndex, src)?.let {
            return it
        }
        val vFile = BookHelp.getImage(book, src)
        if (!vFile.exists()) {
            //todo epub
//            if (book.isEpub()) {
//                EPUBFile.getImage(book, src)?.use { input ->
//                    val newFile = FileUtils.createFileIfNotExist(vFile.absolutePath)
//                    FileOutputStream(newFile).use { output ->
//                        input.copyTo(output)
//                    }
//                }
//            } else

                if (!onUi) {
                runBlocking {
                    BookHelp.saveImage(book, src)
                }
            }
        }
        return try {
            val bitmap = BitmapUtils.decodeBitmap(
                vFile.absolutePath,
                ChapterProvider.visibleWidth,
                ChapterProvider.visibleHeight
            )
            setCache(chapterIndex, src, bitmap)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    fun clearAllCache() {
        cache.forEach { indexCache ->
            indexCache.value.forEach {
                it.value.recycle()
            }
        }
        cache.clear()
    }

    fun clearOut(chapterIndex: Int) {
        cache.forEach { indexCache ->
            if (indexCache.key !in chapterIndex - 1..chapterIndex + 1) {
                indexCache.value.forEach {
                    it.value.recycle()
                }
                cache.remove(indexCache.key)
            }
        }
    }

}
