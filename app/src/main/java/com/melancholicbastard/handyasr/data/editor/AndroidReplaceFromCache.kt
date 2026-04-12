package com.melancholicbastard.handyasr.data.editor

import android.content.Context
import android.util.Log
import com.melancholicbastard.handyasr.domain.editor.ReplaceFromCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidReplaceFromCache(
    private val context: Context
) : ReplaceFromCache {
    companion object {
        private const val TAG = "AndroidReplaceFromCache"
    }

    override suspend fun replaceToPersistentStorage(file: File) {
        withContext(Dispatchers.IO) {
            val recordingsDir = File(context.filesDir, "recordings")
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs()
                Log.d(TAG, "There is no directory on path: ${recordingsDir.absolutePath}")
            }

            try {
                val destFile = File(recordingsDir, "rec_${System.currentTimeMillis()}.wav")
                file.copyTo(destFile, overwrite = true)
            } catch (t: Throwable) {
                Log.e(TAG, "failed to copy file into directory", t)
            }
        }
    }

}