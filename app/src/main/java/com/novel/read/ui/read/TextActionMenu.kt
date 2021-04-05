package com.novel.read.ui.read

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Build
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.view.SupportMenuInflater
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.service.BaseReadAloudService
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.item_text.view.*
import kotlinx.android.synthetic.main.popup_action_menu.view.*
import org.jetbrains.anko.sdk27.listeners.onClick
import org.jetbrains.anko.toast
import java.util.*

@SuppressLint("RestrictedApi")
class TextActionMenu(private val context: Context, private val callBack: CallBack) :
    PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT),
    TextToSpeech.OnInitListener {

    private val adapter = Adapter()
    private val menu = MenuBuilder(context)
    private val moreMenu = MenuBuilder(context)

    init {
        @SuppressLint("InflateParams")
        contentView = LayoutInflater.from(context).inflate(R.layout.popup_action_menu, null)

        isTouchable = true
        isOutsideTouchable = false
        isFocusable = false

        initRecyclerView()
        setOnDismissListener {
            contentView.apply {
                iv_menu_more.setImageResource(R.drawable.ic_more_vert)
                recycler_view_more.gone()
                adapter.setList(menu.visibleItems)
                recycler_view.visible()
            }
        }
    }

    private fun initRecyclerView() = with(contentView) {
        recycler_view.adapter = adapter
        recycler_view_more.adapter = adapter
        SupportMenuInflater(context).inflate(R.menu.content_select_action, menu)
        adapter.setList(menu.visibleItems)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onInitializeMenu(moreMenu)
        }
        if (moreMenu.size() > 0) {
            iv_menu_more.visible()
        }
        iv_menu_more.onClick {
            if (recycler_view.isVisible) {
                iv_menu_more.setImageResource(R.drawable.ic_arrow_back)
                adapter.setList(menu.visibleItems)
                recycler_view.gone()
                recycler_view_more.visible()
            } else {
                iv_menu_more.setImageResource(R.drawable.ic_more_vert)
                recycler_view_more.gone()
                adapter.setList(menu.visibleItems)
                recycler_view.visible()
            }
        }
    }

    inner class Adapter :
        BaseQuickAdapter<MenuItemImpl, BaseViewHolder>(R.layout.item_text){

        override fun convert(holder: BaseViewHolder, item: MenuItemImpl) {
            holder.itemView.run {
                text_view.text = item.title
            }
            holder.itemView.onClick {
                getItem(holder.layoutPosition)?.let {
                    if (!callBack.onMenuItemSelected(it.itemId)) {
                        onMenuItemSelected(it)
                    }
                }
                callBack.onMenuActionFinally()
            }
        }

    }

    private fun onMenuItemSelected(item: MenuItemImpl) {
        when (item.itemId) {
            R.id.menu_copy -> context.sendToClip(callBack.selectedText)
            R.id.menu_aloud -> {
                if (BaseReadAloudService.isRun) {
                    context.toast(R.string.alouding_disable)
                    return
                }
                readAloud(callBack.selectedText)
            }
            else -> item.intent?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    it.putExtra(Intent.EXTRA_PROCESS_TEXT, callBack.selectedText)
                    context.startActivity(it)
                }
            }
        }
    }

    private var textToSpeech: TextToSpeech? = null
    private var ttsInitFinish = false
    private var lastText: String = ""

    @SuppressLint("SetJavaScriptEnabled")
    private fun readAloud(text: String) {
        lastText = text
        if (textToSpeech == null) {
            textToSpeech = TextToSpeech(context, this)
            return
        }
        if (!ttsInitFinish) return
        if (text == "") return
        if (textToSpeech?.isSpeaking == true)
            textToSpeech?.stop()
        textToSpeech?.speak(text, TextToSpeech.QUEUE_ADD, null, "select_text")
        lastText = ""
    }

    @Synchronized
    override fun onInit(status: Int) {
        textToSpeech?.language = Locale.CHINA
        ttsInitFinish = true
        readAloud(lastText)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createProcessTextIntent(): Intent {
        return Intent()
            .setAction(Intent.ACTION_PROCESS_TEXT)
            .setType("text/plain")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getSupportedActivities(): List<ResolveInfo> {
        return context.packageManager
            .queryIntentActivities(createProcessTextIntent(), 0)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createProcessTextIntentForResolveInfo(info: ResolveInfo): Intent {
        return createProcessTextIntent()
            .putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            .setClassName(info.activityInfo.packageName, info.activityInfo.name)
    }

    /**
     * Start with a menu Item order value that is high enough
     * so that your "PROCESS_TEXT" menu items appear after the
     * standard selection menu items like Cut, Copy, Paste.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun onInitializeMenu(menu: Menu) {
        try {
            var menuItemOrder = 100
            for (resolveInfo in getSupportedActivities()) {
                menu.add(
                    Menu.NONE, Menu.NONE,
                    menuItemOrder++, resolveInfo.loadLabel(context.packageManager)
                ).intent = createProcessTextIntentForResolveInfo(resolveInfo)
            }
        } catch (e: Exception) {
            context.toast("获取文字操作菜单出错:${e.localizedMessage}")
        }
    }

    interface CallBack {
        val selectedText: String

        fun onMenuItemSelected(itemId: Int): Boolean

        fun onMenuActionFinally()
    }
}