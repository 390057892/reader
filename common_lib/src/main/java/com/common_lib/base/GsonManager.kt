package com.common_lib.base

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * create by 赵利君 on 2019/10/12
 * describe:
 */
class GsonManager private constructor() {

    fun format(obj: Any): String? {
        var res: String?
        try {
            res = gson!!.toJson(obj)
        } catch (e: Exception) {
            res = null
        }

        return res
    }

    fun getGson(): Gson? {
        return gson
    }

    companion object {
        private var gson: Gson? = null

        private var instance: GsonManager? = null

        @Synchronized
        fun getInstance(): GsonManager {
            if (instance == null) {
                instance = GsonManager()
                if (gson == null) {
                    gson = GsonBuilder().create()
                }
            }
            return instance as GsonManager
        }
    }

}
