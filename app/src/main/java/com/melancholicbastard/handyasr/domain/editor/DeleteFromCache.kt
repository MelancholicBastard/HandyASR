package com.melancholicbastard.handyasr.domain.editor

import java.io.File

interface DeleteFromCache {
    suspend fun deleteFromCache(file: File)
}