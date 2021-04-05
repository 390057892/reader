package com.novel.read.data.db.entity

import com.novel.read.utils.StringUtils
import org.litepal.crud.LitePalSupport
import java.io.Serializable

class SearchHistory(
    var key: String = "",
    var saveTime: Long = 0
) : LitePalSupport(), Serializable {
    override fun equals(other: Any?): Boolean {
        return if (other != null && other.toString() == key) {
            true
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + saveTime.hashCode()
        return result
    }

    fun getBKey(): String {
        return StringUtils.convertCC(key)
    }
}