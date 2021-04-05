package com.novel.read.data.model

data class AppUpdateResp(
    val appEdition: AppEdition
)

data class AppEdition(
    val createTime: String,
    val editionCode: String,
    val editionId: Int,
    val fileUrl: String,
    val forceUpdate: Int,
    val insideEditionCode: Int,
    val isDel: Int,
    val packageSize: String,
    val pushTime: String,
    val shellName: String,
    val status: Int,
    val upgradeContent: String
)