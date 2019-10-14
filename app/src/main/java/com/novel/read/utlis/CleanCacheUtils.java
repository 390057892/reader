package com.novel.read.utlis;

import android.content.Context;
import android.os.Environment;

import com.novel.read.model.db.BookChapterBean;
import com.novel.read.model.db.CollBookBean;
import com.novel.read.model.db.SearchListTable;

import org.litepal.LitePal;

import java.io.File;
import java.math.BigDecimal;

/**
 * @author: LiJun 390057892@qq.com
 * @date: 2018/4/11 16:00
 */

public class CleanCacheUtils {

    private static CleanCacheUtils manager;

    public static CleanCacheUtils getInstance() {
        return manager == null ? (manager = new CleanCacheUtils()) : manager;
    }

    /**
     * @param context
     * @return
     * @throws Exception
     *             获取当前缓存
     */
    public String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    /**
     * @param context
     *            删除缓存
     */
    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int size = 0;
            if (children != null) {
                size = children.length;
                for (int i = 0; i < size; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }

        }
        if (dir == null) {
            return true;
        } else {

            return dir.delete();
        }
    }

    // 获取文件
    // Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/
    // 目录，一般放一些长时间保存的数据
    // Context.getExternalCacheDir() -->
    // SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            int size2 = 0;
            if (fileList != null) {
                size2 = fileList.length;
                for (int i = 0; i < size2; i++) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory()) {
                        size = size + getFolderSize(fileList[i]);
                    } else {
                        size = size + fileList[i].length();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     * 计算缓存的大小
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            // return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }


    /**
     * 清除缓存
     *
     * @param clearReadPos 是否删除阅读记录
     */
    public synchronized void clearCache(boolean clearReadPos, boolean clearCollect,Context context) {
        try {
            clearAllCache(context);
            // 删除搜索记录（SharePreference）
            if (clearReadPos) {
                LitePal.deleteAll(SearchListTable.class);
            }
            // 清空书架
            if (clearCollect) {
                LitePal.deleteAll(CollBookBean.class);
                LitePal.deleteAll(BookChapterBean.class);
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

}
