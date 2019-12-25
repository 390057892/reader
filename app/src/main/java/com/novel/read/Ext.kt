package com.novel.read

import android.content.Context
import android.widget.ImageView
import android.widget.Toast

fun Context.showToast(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}

fun Context.getScreenContentWidth(): Int {
    val displayMetrics = this.resources.displayMetrics
    return displayMetrics.widthPixels
}

fun Context.dp2px(dps: Int): Int {
    return Math.round(dps.toFloat() * getDensityDpiScale(this))
}

fun getDensityDpiScale(context: Context): Float {
    return context.resources.displayMetrics.xdpi / 160.0f
}