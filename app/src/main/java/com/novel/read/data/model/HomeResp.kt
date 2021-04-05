package com.novel.read.data.model

data class HomeResp(
    val wordNumRank: List<ClickRank>,
    val clickRank: List<EndRank>,
    val starRank: List<HotRank>,
    val recommendRank: List<RecommendRank>,

    val boyHotRank: List<BoyHotRank>,
    val boyEndRank: List<BoyEndRank>,
    val boyHotSearchRank: List<BoySearchRank>,

    val girlHotRank: List<GirlHotRank>,
    val girlEndRank: List<GirlEndRank>,
    val girlHotSearchRank: List<GirlSearchRank>,
)