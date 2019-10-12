package com.mango.mangolib.http

import android.util.Log
import androidx.annotation.NonNull
import com.common_lib.base.GsonManager
import com.mango.mangolib.event.BaseEvent
import com.mango.mangolib.event.EventManager
import com.mango.mangolib.event.GenericBaseEvent
import com.mango.mangolib.event.HTTPReponseErrorEvent
import com.squareup.otto.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * create by zlj on 2019/10/12
 * describe:
 */
class ServiceCallback<T>(resultEventClass: Class<out BaseEvent<*>>) : Callback<T> {
    protected var event: BaseEvent<T>

    init {
        EventManager.getInstance().registerSubscriber(this)
        try {
            event = resultEventClass.newInstance() as BaseEvent<T>
        } catch (e: InstantiationException) {
            event = BaseEvent()
        } catch (e: IllegalAccessException) {
            event = BaseEvent()
        }

    }

    /**
     * 返回 false 表示子类已经处理好了事件，父类不需要再抛出事件。
     * 返回 true 表示 父类还需要抛出事件。
     * 默认返回 true
     *
     * @param result
     */
    protected fun onSuccess(result: T?): Boolean {
        //        ErrorResponse response=extractEr(result);
        return true
    }

    /**
     * 返回 false 表示子类已经处理好了事件，父类不需要再抛出事件。
     * 返回 true 表示 父类还需要抛出事件。
     * 默认返回 true
     *
     * @param er
     */
    protected fun onFailure(er: ErrorResponse): Boolean {
        return true
    }


    /**
     * 错误时，会读取出 Response 的 body 并解析为 ErrorResponse
     * 此时 在 onResponse 方法里就取不出 body 了。
     * 因此 TokenAuthenticator 就 post 出这个 event，被本类捕获。
     *
     * @param event
     */
    @Subscribe
    fun errorHappended(event: HTTPReponseErrorEvent) {
        this.event.er = event.errorResponse
    }


    private fun extractEr(response: Response<*>): ErrorResponse? {
        try {
            if (response.code() == 200) {
                val str = response.errorBody()!!.string()
                val gson = GsonManager.getInstance().getGson()
                val er = gson!!.fromJson(str, ErrorResponse::class.java)
                er.data = str
                return er
            } else {
                Log.e("ServiceCallBack", "服务器异常")
                val er = ErrorResponse()
                er.code = -9999
                er.msg = "服务器异常"
                return er
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    override fun onResponse(@NonNull call: Call<T>, @NonNull response: Response<T>) {
        EventManager.getInstance().unregisterSubscriber(this)
        if (response.isSuccessful) {
            val resBody = response.body()
            event.result = resBody
            if (onSuccess(resBody)) {
                EventManager.getInstance().postEvent(event)
            }
        } else {
            val extracted = extractEr(response)
            if (extracted != null) {
                event.er = extracted
            }

            val er = event.er
            if (onFailure(er!!)) {
                EventManager.getInstance().postEvent(event)
            }
            // 单独抛出一个错误事件
            EventManager.getInstance().postEvent(GenericBaseEvent(er))
        }
    }

    override fun onFailure(@NonNull call: Call<T>, t: Throwable) {
        EventManager.getInstance().unregisterSubscriber(this)
        val er = ErrorResponse()
        er.code = -9999
        er.msg = "网络异常"
        event.er = er
        EventManager.getInstance().postEvent(event)
        t.printStackTrace()
    }
}
