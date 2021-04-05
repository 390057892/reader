package com.novel.read.help

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.novel.read.utils.ext.isAbsUrl
import com.novel.read.utils.ext.isContentPath
import java.io.File

object ImageLoader {

    /**
     * 自动判断path类型
     */
    fun load(context: Context, path: String?): RequestBuilder<Drawable> {
        return when {
            path.isNullOrEmpty() -> Glide.with(context).load(path)
            path.isAbsUrl() -> Glide.with(context).load(path)
            path.isContentPath() -> Glide.with(context).load(Uri.parse(path))
            else -> try {
                Glide.with(context).load(File(path))
            } catch (e: Exception) {
                Glide.with(context).load(path)
            }
        }
    }

    fun load(context: Context, @DrawableRes resId: Int?): RequestBuilder<Drawable> {
        return Glide.with(context).load(resId)
    }

    fun load(context: Context, file: File?): RequestBuilder<Drawable> {
        return Glide.with(context).load(file)
    }

    fun load(context: Context, uri: Uri?): RequestBuilder<Drawable> {
        return Glide.with(context).load(uri)
    }

    fun load(context: Context, drawable: Drawable?): RequestBuilder<Drawable> {
        return Glide.with(context).load(drawable)
    }

    fun load(context: Context, bitmap: Bitmap?): RequestBuilder<Drawable> {
        return Glide.with(context).load(bitmap)
    }

    fun load(context: Context, bytes: ByteArray?): RequestBuilder<Drawable> {
        return Glide.with(context).load(bytes)
    }

}
