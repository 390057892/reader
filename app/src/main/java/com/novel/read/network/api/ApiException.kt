package com.novel.read.network.api

class ApiException(var code: Int, override var message: String) : RuntimeException()