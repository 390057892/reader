package com.novel.read.network.repository

import com.novel.read.network.ServiceCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepository {

    suspend fun getImage(src: String) = withContext(Dispatchers.IO) {
        ServiceCreator.apiService1.getImage(src)
    }
}