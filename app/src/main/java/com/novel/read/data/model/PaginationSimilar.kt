package com.novel.read.data.model

import androidx.annotation.Keep


@Keep
data class PaginationSimilar<T>(
    val recommendBookList: MutableList<T>
)