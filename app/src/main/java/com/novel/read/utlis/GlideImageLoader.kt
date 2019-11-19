package com.novel.read.utlis

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView

import androidx.annotation.DrawableRes

import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.novel.read.R


/**
 * create by zlj on 2018/11/21
 * describe:
 */
object GlideImageLoader {

    private//设置错误图片
    //设置占位图片
    val requestHeadOptions: RequestOptions
        get() = RequestOptions()
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .diskCacheStrategy(DiskCacheStrategy.ALL)

    fun displayImage(context: Context, path: String, imageView: ImageView) {
        Glide.with(context)
            .load(path)
            .into(imageView)
    }

    //加载矩形圆角图片
    fun displayCornerImage(context: Context, path: String, imageView: ImageView) {
        //设置图片圆角角度
        val roundedCorners = RoundedCorners(10)
        val options = RequestOptions.bitmapTransform(roundedCorners)
            .placeholder(R.drawable.cover_default)
            .error(R.drawable.cover_default)


        Glide.with(context)
            .load(path)
            .apply(options)
            .into(imageView)

    }



    //加载矩形圆角图片 自动设置占位图
    fun displayCornerImage(context: Context, path: String, imageView: ImageView, defaultId: Int) {
        //设置图片圆角角度
        val roundedCorners = RoundedCorners(15)
        val options = RequestOptions.bitmapTransform(roundedCorners)
            .placeholder(defaultId)
            .error(defaultId)
        Glide.with(context)
            .load(path)
            .apply(options)
            .into(imageView)

    }

    //加载圆形图片
    @SuppressLint("CheckResult")
    fun loadCirCleImage(
        context: Context,
        path: String,
        imageView: ImageView,
        width: Int,
        height: Int
    ) {
        val myOptions = requestHeadOptions.circleCrop()
        myOptions.error(R.mipmap.ic_launcher)
        myOptions.placeholder(R.mipmap.ic_launcher)
        Glide.with(context.applicationContext)
            .load(path)
            .apply(myOptions)
            .into(imageView)
    }

}
