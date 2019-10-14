package com.mango.mangolib.event

import android.os.Handler
import android.os.Looper
import com.squareup.otto.Bus

/**
 * create by zlj on 2019/10/12
 * describe:
 */
class EventManager private constructor() {

    private val bus: MainThreadBus

    init {
        bus = MainThreadBus()
    }

    fun registerSubscriber(subscriber: Any) {
        try {
            bus.register(subscriber)
        } catch (e: Exception) {
        }

    }

    fun unregisterSubscriber(subscriber: Any) {
        try {
            bus.unregister(subscriber)
        } catch (e: Exception) {
        }

    }

    fun postEvent(event: OttoEventInterface) {
        bus.post(event)
    }

    internal inner class MainThreadBus : Bus() {
        private val mHandler = Handler(Looper.getMainLooper())

        override fun post(event: Any) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event)
            } else {
                mHandler.post { super@MainThreadBus.post(event) }
            }
        }

        override fun register(`object`: Any) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.register(`object`)
            } else {
                mHandler.post { super@MainThreadBus.register(`object`) }
            }
        }

        override fun unregister(`object`: Any) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.unregister(`object`)
            } else {
                mHandler.post { super@MainThreadBus.unregister(`object`) }
            }
        }
    }

    companion object {

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            EventManager()
        }
    }
}
