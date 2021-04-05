package com.novel.read.data.model

import com.novel.read.App
import com.novel.read.R
import com.novel.read.constant.LayoutType
import kotlin.collections.ArrayList


data class TypeName(
    val rankType: Int,
    val name: String,
    var check: Boolean
) {


}

fun rankList(layoutType: Int): List<TypeName> {
    val list: MutableList<TypeName> =
        ArrayList<TypeName>()
    list.clear()
    list.add(TypeName(LayoutType.HOT, App.INSTANCE.getString(R.string.big_hot_title), false))
    list.add(TypeName(LayoutType.CLICK, App.INSTANCE.getString(R.string.click_title), false))
    list.add(
        TypeName(
            LayoutType.RECOMMEND,
            App.INSTANCE.getString(R.string.recommend_title),
            false
        )
    )
    list.add(TypeName(LayoutType.END, App.INSTANCE.getString(R.string.end_title), false))

    list[layoutType - 1].check = true
    return list
}

fun manList(layoutType: Int): List<TypeName> {
    val list: MutableList<TypeName> =
        ArrayList<TypeName>()
    list.clear()
    list.add(TypeName(LayoutType.BOY_HOT, App.INSTANCE.getString(R.string.big_hot_title), false))
    list.add(TypeName(LayoutType.BOY_END, App.INSTANCE.getString(R.string.end_title2), false))
    list.add(
        TypeName(
            LayoutType.BOY_SEARCH,
            App.INSTANCE.getString(R.string.hot_search_title),
            false
        )
    )
    list[layoutType - 6].check = true
    return list
}

fun womanList(layoutType: Int): List<TypeName> {
    val list: MutableList<TypeName> =
        ArrayList<TypeName>()
    list.clear()
    list.add(TypeName(LayoutType.GIRL_HOT, App.INSTANCE.getString(R.string.big_hot_title), false))
    list.add(TypeName(LayoutType.GIRL_END, App.INSTANCE.getString(R.string.end_title2), false))
    list.add(
        TypeName(
            LayoutType.GIRL_SEARCH,
            App.INSTANCE.getString(R.string.hot_search_title),
            false
        )
    )
    list[layoutType - 9].check = true
    return list
}