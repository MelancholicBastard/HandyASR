package com.melancholicbastard.handyasr.data.editor

import android.util.Log
import com.melancholicbastard.handyasr.domain.editor.DeleteFromCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidDeleteFromCache : DeleteFromCache {
    companion object {
        private const val TAG = "AndroidDeleteFromCache"
    }

    override suspend fun deleteFromCache(file: File) {
        withContext(Dispatchers.IO) {
            try {
                file.delete()
            } catch (t: Throwable) {
                Log.e(TAG, "failed to delete file from cache", t)
            }
        }
    }
}