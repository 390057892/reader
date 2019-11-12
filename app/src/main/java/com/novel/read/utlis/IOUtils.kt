package com.novel.read.utlis

import java.io.Closeable
import java.io.IOException

/**
 * Created by zlj
 */

object IOUtils {

    fun close(closeable: Closeable?) {
        if (closeable == null) return
        try {
            closeable.close()
        } catch (e: IOException) {
            e.printStackTrace()
            //close error
        }

    }
}
