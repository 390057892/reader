package com.novel.read.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import com.novel.read.App
import com.novel.read.constant.EventBus
import com.novel.read.data.db.entity.Book
import com.novel.read.help.ActivityHelp
import com.novel.read.service.BaseReadAloudService
import com.novel.read.service.help.ReadAloud
import com.novel.read.ui.MainActivity
import com.novel.read.ui.read.ReadBookActivity
import com.novel.read.utils.ext.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by zlj on 2019/1/6.
 * 监听耳机键
 */

class MediaButtonReceiver : BroadcastReceiver() {

    companion object {

        fun handleIntent(context: Context, intent: Intent): Boolean {
            val intentAction = intent.action
            if (Intent.ACTION_MEDIA_BUTTON == intentAction) {
                val keyEvent =
                    intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return false
                val keycode: Int = keyEvent.keyCode
                val action: Int = keyEvent.action
                if (action == KeyEvent.ACTION_DOWN) {
                    when (keycode) {
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                        }
                        KeyEvent.KEYCODE_MEDIA_NEXT -> {
                        }
                        else -> readAloud(context)
                    }
                }
            }
            return true
        }

        private fun readAloud(context: Context) {
            when {
                BaseReadAloudService.isRun -> if (BaseReadAloudService.isPlay()) {
                    ReadAloud.pause(context)
                } else {
                    ReadAloud.resume(context)
                }

                ActivityHelp.isExist(ReadBookActivity::class.java) ->
                    postEvent(EventBus.MEDIA_BUTTON, true)
                else -> if (context.getPrefBoolean("mediaButtonOnExit", true)) {
                    GlobalScope.launch(Main) {
                        val lastBook: Book? = withContext(IO) {
                            App.db.getBookDao().lastReadBook()
                        }
                        lastBook?.let {
                            if (!ActivityHelp.isExist(MainActivity::class.java)) {
                                Intent(context, MainActivity::class.java).let {
                                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(it)
                                }
                            }
                            Intent(context, ReadBookActivity::class.java).let {
                                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                it.putExtra("readAloud", true)
                                context.startActivity(it)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (handleIntent(context, intent) && isOrderedBroadcast) {
            abortBroadcast()
        }
    }

}
