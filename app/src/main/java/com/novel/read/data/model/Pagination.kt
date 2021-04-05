package com.novel.read.data.model

import androidx.annotation.Keep

/**
 * Created by xiaojianjun on 2019-11-07.
 */
@Keep
data class Pagination<T>(
    val count: Int,
    val bookList: MutableList<T>,
    val bookRankList: MutableList<T>,
    val searchTermsList: MutableList<T>
)