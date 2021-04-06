package com.novel.read.data.model

/**
 * @description: 参数校验DTO
 * @author: zlj
 * @date: 2020/9/25 9:50
 */
class CheckSumDTO(
    /**
     * 对外接口接入方 appid
     */
    private var appid: String,
    /**
     * 随机数，最大128位
     */
    private var nonce: String,
    /**
     * 当前UTC时间戳，从1970年1月1日0点0 分0 秒开始到现在的秒数(String)，接口调用有效期为该时间戳起5分钟内
     */
    private var curtime: String,
    /**
     * SHA1(AppSecret+Nonce+curTime),三个参数拼接的字符串，进行SHA1哈希计算，
     * 转化成16进制字符(String, 小写)。其中AppSecret为平台分配的Appid对应的AppSecret密钥
     */
    private var checksum: String
)