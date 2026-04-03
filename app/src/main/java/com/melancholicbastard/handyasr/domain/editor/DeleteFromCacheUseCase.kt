package com.melancholicbastard.handyasr.domain.editor

import java.io.File

class DeleteFromCacheUseCase(
    private val deleteFromCache : DeleteFromCache
) {
    suspend operator fun invoke(file: File) {
        deleteFromCache.deleteFromCache(file)
    }
}