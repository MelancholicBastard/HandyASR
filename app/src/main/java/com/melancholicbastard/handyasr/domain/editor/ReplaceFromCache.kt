package com.melancholicbastard.handyasr.domain.editor

import java.io.File

interface ReplaceFromCache {
    suspend fun replaceToPersistentStorage(file: File): File
}