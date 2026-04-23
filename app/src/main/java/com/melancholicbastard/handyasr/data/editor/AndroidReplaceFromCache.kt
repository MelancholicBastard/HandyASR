package com.melancholicbastard.handyasr.data.editor

import android.util.Log
import com.melancholicbastard.handyasr.domain.editor.ReplaceFromCache
import com.melancholicbastard.handyasr.presentation.di.annotation.RecordingsDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AndroidReplaceFromCache @Inject constructor(
    @param:RecordingsDir
    private val recordingsDir: File
) : ReplaceFromCache {
    companion object {
        private const val TAG = "AndroidReplaceFromCache"
    }

    override suspend fun replaceToPersistentStorage(file: File): File {
        return withContext(Dispatchers.IO) {
            try {
                val destFile = File(recordingsDir, "rec_${System.currentTimeMillis()}.wav")
                file.copyTo(destFile, overwrite = true)
                destFile
            } catch (t: Throwable) {
                Log.e(TAG, "failed to copy file into directory", t)
                throw t
            }
        }
    }

}