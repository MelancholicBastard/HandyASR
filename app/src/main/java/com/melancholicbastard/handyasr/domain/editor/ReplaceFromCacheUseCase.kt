package com.melancholicbastard.handyasr.domain.editor

import java.io.File

class ReplaceFromCacheUseCase(
    private val replaceFromCache : ReplaceFromCache
) {
    suspend operator fun invoke(file: File): File {
        return replaceFromCache.replaceToPersistentStorage(file)
    }
}