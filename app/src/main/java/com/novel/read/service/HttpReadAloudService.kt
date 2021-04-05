package com.novel.read.service

import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import com.novel.read.constant.EventBus
import com.novel.read.help.IntentHelp
import com.novel.read.service.help.ReadBook
import com.novel.read.utils.FileUtils
import com.novel.read.utils.ext.*
import com.novel.read.help.coroutine.Coroutine
import kotlinx.coroutines.isActive
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream

class HttpReadAloudService : BaseReadAloudService(),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private val mediaPlayer = MediaPlayer()
    private lateinit var ttsFolder: String
    private var task: Coroutine<*>? = null
    private var playingIndex = -1

    override fun onCreate() {
        super.onCreate()
        ttsFolder = externalCacheDir!!.absolutePath + File.separator + "httpTTS"
        mediaPlayer.setOnErrorListener(this)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnCompletionListener(this)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        task?.cancel()
        mediaPlayer.release()
    }

    override fun newReadAloud(dataKey: String?, play: Boolean) {
        mediaPlayer.reset()
        playingIndex = -1
        super.newReadAloud(dataKey, play)
    }

    override fun play() {
        if (contentList.isEmpty()) return
        if (nowSpeak == 0) {
            downloadAudio()
        } else {
            val file = getSpeakFile(nowSpeak)
            if (file.exists()) {
                playAudio(FileInputStream(file).fd)
            }
        }
    }

    private fun downloadAudio() {
        task?.cancel()
        task = execute {
            FileUtils.deleteFile(ttsFolder)
            for (index in 0 until contentList.size) {
                if (isActive) {
//                    ReadAloud.httpTTS?.let {
//                        AnalyzeUrl(
//                            it.url,
//                            speakText = contentList[index],
//                            speakSpeed = AppConfig.ttsSpeechRate
//                        ).getResponseBytes()?.let { bytes ->
//                            if (isActive) {
//                                val file = getSpeakFile(index)
//                                file.writeBytes(bytes)
//                                if (index == nowSpeak) {
//                                    @Suppress("BlockingMethodInNonBlockingContext")
//                                    val fis = FileInputStream(file)
//                                    playAudio(fis.fd)
//                                }
//                            }
//                        }
//                    }
                } else {
                    break
                }
            }
        }
    }

    @Synchronized
    private fun playAudio(fd: FileDescriptor) {
        if (playingIndex != nowSpeak && requestFocus()) {
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(fd)
                mediaPlayer.prepareAsync()
                playingIndex = nowSpeak
                postEvent(EventBus.TTS_PROGRESS, readAloudNumber + 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getSpeakFile(index: Int = nowSpeak): File {
        return FileUtils.createFileIfNotExist("${ttsFolder}${File.separator}${index}.mp3")
    }

    override fun pauseReadAloud(pause: Boolean) {
        super.pauseReadAloud(pause)
        mediaPlayer.pause()
    }

    override fun resumeReadAloud() {
        super.resumeReadAloud()
        if (playingIndex == -1) {
            play()
        } else {
            mediaPlayer.start()
        }
    }

    /**
     * 更新朗读速度
     */
    override fun upSpeechRate(reset: Boolean) {
        task?.cancel()
        mediaPlayer.stop()
        playingIndex = -1
        downloadAudio()
    }

    /**
     * 上一段
     */
    override fun prevP() {
        if (nowSpeak > 0) {
            mediaPlayer.stop()
            nowSpeak--
            readAloudNumber -= contentList[nowSpeak].length.minus(1)
            play()
        }
    }

    /**
     * 下一段
     */
    override fun nextP() {
        if (nowSpeak < contentList.size - 1) {
            mediaPlayer.stop()
            readAloudNumber += contentList[nowSpeak].length.plus(1)
            nowSpeak++
            play()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        super.play()
        if (pause) return
        mp?.start()
        textChapter?.let {
            if (readAloudNumber + 1 > it.getReadLength(pageIndex + 1)) {
                pageIndex++
                ReadBook.moveToNextPage()
            }
        }
        postEvent(EventBus.TTS_PROGRESS, readAloudNumber + 1)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        if (what == -38 && extra == 0) {
            return true
        }
        handler.postDelayed({
            readAloudNumber += contentList[nowSpeak].length + 1
            if (nowSpeak < contentList.lastIndex) {
                nowSpeak++
                play()
            } else {
                nextChapter()
            }
        }, 50)
        return true
    }

    override fun onCompletion(mp: MediaPlayer?) {
        readAloudNumber += contentList[nowSpeak].length + 1
        if (nowSpeak < contentList.lastIndex) {
            nowSpeak++
            play()
        } else {
            nextChapter()
        }
    }

    override fun aloudServicePendingIntent(actionStr: String): PendingIntent? {
        return IntentHelp.servicePendingIntent<HttpReadAloudService>(this, actionStr)
    }
}