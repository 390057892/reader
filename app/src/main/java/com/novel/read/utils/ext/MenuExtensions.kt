package com.novel.read.utils.ext

import android.annotation.SuppressLint
import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.core.view.forEach
import com.novel.read.R
import com.novel.read.constant.Theme
import com.novel.read.utils.DrawableUtils
import com.novel.read.utils.UIUtils
import java.lang.reflect.Method
import java.util.*

@SuppressLint("RestrictedApi")
fun Menu.applyTint(context: Context, theme: Theme = Theme.Auto): Menu = this.let { menu ->
    if (menu is MenuBuilder) {
        menu.setOptionalIconsVisible(true)
    }
    val defaultTextColor = context.getCompatColor(R.color.primaryText)
    val tintColor = UIUtils.getMenuColor(context, theme)
    menu.forEach { item ->
        (item as MenuItemImpl).let { impl ->
            //overflow：展开的item
            DrawableUtils.setTint(
                impl.icon,
                if (impl.requiresOverflow()) defaultTextColor
                else tintColor
            )
        }
    }
    return menu
}

fun Menu.applyOpenTint(context: Context) {
    //展开菜单显示图标
    if (this.javaClass.simpleName.equals("MenuBuilder", ignoreCase = true)) {
        val defaultTextColor = context.getCompatColor(R.color.primaryText)
        try {
            var method: Method =
                this.javaClass.getDeclaredMethod("setOptionalIconsVisible", java.lang.Boolean.TYPE)
            method.isAccessible = true
            method.invoke(this, true)
            method = this.javaClass.getDeclaredMethod("getNonActionItems")
            val menuItems = method.invoke(this)
            if (menuItems is ArrayList<*>) {
                for (menuItem in menuItems) {
                    if (menuItem is MenuItem) {
                        DrawableUtils.setTint(
                            menuItem.icon,
                            defaultTextColor
                        )
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }
}