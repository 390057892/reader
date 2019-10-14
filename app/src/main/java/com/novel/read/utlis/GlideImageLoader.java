package com.novel.read.utlis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.novel.read.R;


/**
 * create by 赵利君 on 2018/11/21
 * describe:
 */
public class GlideImageLoader {

    public static void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .into(imageView);
    }

    //加载矩形圆角图片
    public static void displayCornerImage(Context context, String path, ImageView imageView) {
        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(10);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners)
                .placeholder(R.drawable.cover_default)
                .error(R.drawable.cover_default)
                ;


        Glide.with(context)
                .load(path)
                .apply(options)
//                .thumbnail(loadTransform(context,R.drawable.cover_default,5))
//                .thumbnail(loadTransform(context,R.drawable.cover_default,5))
                .into(imageView);

    }
    private static RequestBuilder<Drawable> loadTransform(Context context, @DrawableRes int placeholderId, float radius) {

        return Glide.with(context)
                .load(placeholderId)
                .apply(new RequestOptions().centerCrop()
                        .transform(new GlideRoundTransform(context, (int) radius)));

    }



    //加载矩形圆角图片 自动设置占位图
    public static void displayCornerImage(Context context, String path, ImageView imageView,int defaultId) {
        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(15);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners)
                .placeholder(defaultId)
                .error(defaultId);
        Glide.with(context)
                .load(path)
                .apply(options)
                .into(imageView);

    }

    //加载圆形图片
    @SuppressLint("CheckResult")
    public static void loadCirCleImage(Context context, String path, ImageView imageView, int width, int height) {
        RequestOptions myOptions = getRequestHeadOptions().circleCrop();
        myOptions.error(R.mipmap.ic_launcher);
        myOptions.placeholder(R.mipmap.ic_launcher);
        Glide.with(context.getApplicationContext())
                .load(path)
                .apply(myOptions)
                .into(imageView);
    }

    @NonNull
    private static RequestOptions getRequestHeadOptions() {
        return new RequestOptions()
                .error(R.mipmap.ic_launcher)//设置错误图片
                .placeholder(R.mipmap.ic_launcher)     //设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

}
