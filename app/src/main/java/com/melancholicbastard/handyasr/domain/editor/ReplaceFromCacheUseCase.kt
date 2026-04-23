package com.melancholicbastard.handyasr.domain.editor

import java.io.File
import javax.inject.Inject

class ReplaceFromCacheUseCase @Inject constructor(
    private val replaceFromCache : ReplaceFromCache
) {
    suspend operator fun invoke(file: File): File {
        return replaceFromCache.replaceToPersistentStorage(file)
    }
}