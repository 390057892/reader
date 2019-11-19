package com.novel.read.utlis

/**
 * 编码类型
 */
enum class Charset private constructor(var code: String) {
    UTF8("UTF-8"),
    UTF16LE("UTF-16LE"),
    UTF16BE("UTF-16BE"),
    GBK("GBK");


    companion object {
        val BLANK: Byte = 0x0a
    }
}
