package com.melancholicbastard.handyasr.domain.editor

import java.io.File
import javax.inject.Inject

class DeleteFromCacheUseCase @Inject constructor(
    private val deleteFromCache : DeleteFromCache
) {
    suspend operator fun invoke(file: File) {
        deleteFromCache.deleteFromCache(file)
    }
}