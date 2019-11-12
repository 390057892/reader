package com.novel.read.utlis

import android.annotation.SuppressLint


import com.novel.read.constants.Constant

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@SuppressLint("SimpleDateFormat")
object DateUtli {

    private val HOUR_OF_DAY = 24
    private val DAY_OF_YESTERDAY = 2
    private val TIME_UNIT = 60

    /**
     * 时间戳格式转换
     */
    private val dayNames = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")

    //将时间转换成日期
    fun dateConvert(time: Long, pattern: String): String {
        val date = Date(time)
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat(pattern)
        return format.format(date)
    }


    fun dateConvert(timesamp: Long, flag: Int): String {
        var time = timesamp
        time *= 1000
        val result: String
        val todayCalendar = Calendar.getInstance()
        val otherCalendar = Calendar.getInstance()
        otherCalendar.timeInMillis = time

        val timeFormat = "M月d日"
        val yearTimeFormat = "yyyy年M月d日"

        val yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR)
        if (yearTemp) {
            val todayMonth = todayCalendar.get(Calendar.MONTH)
            val otherMonth = otherCalendar.get(Calendar.MONTH)
            if (todayMonth == otherMonth) {//表示是同一个月
                when (todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE)) {
                    0 -> result = getHourAndMin(time)
                    1 -> if (flag == 1) {
                        result = "昨天 "
                    } else {
                        result = "昨天 " + getHourAndMin(time)
                    }
                    2, 3, 4, 5, 6 -> {
                        val dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH)
                        val todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH)
                        if (dayOfMonth == todayOfMonth) {//表示是同一周
                            val dayOfWeek = otherCalendar.get(Calendar.DAY_OF_WEEK)
                            if (dayOfWeek != 1) {//判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
                                result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1]
                            } else {
                                result = getTime(time, timeFormat)
                            }
                        } else {
                            result = getTime(time, timeFormat)
                        }
                    }
                    else -> result = getTime(time, timeFormat)
                }
            } else {
                result = getTime(time, timeFormat)
            }
        } else {
            result = getYearTime(time, yearTimeFormat)
        }
        return result
    }

    /**
     * 当天的显示时间格式
     */
    private fun getHourAndMin(time: Long): String {
        val format = SimpleDateFormat("HH:mm")
        return format.format(Date(time))
    }

    /**
     * 不同一周的显示时间格式
     */
    private fun getTime(time: Long, timeFormat: String): String {
        val format = SimpleDateFormat(timeFormat)
        return format.format(Date(time))
    }

    /**
     * 不同年的显示时间格式
     */
    private fun getYearTime(time: Long, yearTimeFormat: String): String {
        val format = SimpleDateFormat(yearTimeFormat)
        return format.format(Date(time))
    }

}
