package com.novel.read.utlis

import java.util.*

/**
 * create by zlj on 2019/7/15
 * describe:
 */
class MyTimeTask(private val time: Long, private val task: TimerTask?) {

    private var timer: Timer? = null

    init {
        if (timer == null) {
            timer = Timer()
        }
    }

    fun start() {
        timer!!.schedule(task, 0, time)//每隔time时间段就执行一次
    }

    fun stop() {
        if (timer != null) {
            timer!!.cancel()
            task?.cancel()
        }
    }
}
