package com.novel.read.utlis

import android.content.Context
import android.os.Environment

import com.novel.read.model.db.BookChapterBean
import com.novel.read.model.db.CollBookBean
import com.novel.read.model.db.SearchListTable

import org.litepal.LitePal

import java.io.File
import java.math.BigDecimal

/**
 * @author: LiJun 390057892@qq.com
 * @date: 2018/4/11 16:00
 */

class CleanCacheUtils {

    /**
     * @param context
     * @return
     * @throws Exception
     * 获取当前缓存
     */
    @Throws(Exception::class)
    fun getTotalCacheSize(context: Context): String {
        var cacheSize = getFolderSize(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheSize += getFolderSize(context.externalCacheDir)
        }
        return getFormatSize(cacheSize.toDouble())
    }


    /**
     * 清除缓存
     *
     * @param clearReadPos 是否删除阅读记录
     */
    @Synchronized
    fun clearCache(clearReadPos: Boolean, clearCollect: Boolean, context: Context) {
        try {
            clearAllCache(context)
            // 删除搜索记录（SharePreference）
            if (clearReadPos) {
                LitePal.deleteAll(SearchListTable::class.java)
            }
            // 清空书架
            if (clearCollect) {
                LitePal.deleteAll(CollBookBean::class.java)
                LitePal.deleteAll(BookChapterBean::class.java)
            }
        } catch (e: Exception) {
        }

    }

    companion object {

        private var instance: CleanCacheUtils? = null

        @Synchronized
        fun getInstance(): CleanCacheUtils {
            if (instance == null) {
                instance = CleanCacheUtils()
            }
            return instance as CleanCacheUtils
        }

        /**
         * @param context
         * 删除缓存
         */
        fun clearAllCache(context: Context) {
            deleteDir(context.cacheDir)
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                deleteDir(context.externalCacheDir)
            }
        }

        private fun deleteDir(dir: File?): Boolean {
            if (dir != null && dir.isDirectory) {
                val children = dir.list()
                var size = 0
                if (children != null) {
                    size = children.size
                    for (i in 0 until size) {
                        val success = deleteDir(File(dir, children[i]))
                        if (!success) {
                            return false
                        }
                    }
                }

            }
            return dir?.delete() ?: true
        }

        // 获取文件
        // Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/
        // 目录，一般放一些长时间保存的数据
        // Context.getExternalCacheDir() -->
        // SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
        @Throws(Exception::class)
        fun getFolderSize(file: File?): Long {
            var size: Long = 0
            try {
                val fileList = file!!.listFiles()
                var size2 = 0
                if (fileList != null) {
                    size2 = fileList.size
                    for (i in 0 until size2) {
                        // 如果下面还有文件
                        if (fileList[i].isDirectory) {
                            size = size + getFolderSize(fileList[i])
                        } else {
                            size = size + fileList[i].length()
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return size
        }

        /**
         * 格式化单位
         * 计算缓存的大小
         * @param size
         * @return
         */
        fun getFormatSize(size: Double): String {
            val kiloByte = size / 1024
            if (kiloByte < 1) {
                // return size + "Byte";
                return "0K"
            }

            val megaByte = kiloByte / 1024
            if (megaByte < 1) {
                val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
                return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB"
            }

            val gigaByte = megaByte / 1024
            if (gigaByte < 1) {
                val result2 = BigDecimal(java.lang.Double.toString(megaByte))
                return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB"
            }

            val teraBytes = gigaByte / 1024
            if (teraBytes < 1) {
                val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB"
            }
            val result4 = BigDecimal(teraBytes)
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
        }
    }

}
