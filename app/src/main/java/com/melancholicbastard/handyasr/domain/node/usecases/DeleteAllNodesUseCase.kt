package com.melancholicbastard.handyasr.domain.node.usecases

import android.util.Log
import com.melancholicbastard.handyasr.domain.node.NodeRepository
import com.melancholicbastard.handyasr.presentation.di.annotation.RecordingsDir
import java.io.File
import javax.inject.Inject

class DeleteAllNodesUseCase @Inject constructor(
    private val repository: NodeRepository,
    @param:RecordingsDir
    private val recordingsDir: File
) {
    companion object {
        private const val TAG = "DeleteAllNodesUseCase"
    }

    suspend operator fun invoke() {
        try {
            recordingsDir.listFiles()?.forEach { it.delete() }
        } catch (t: Throwable) {
            Log.e(TAG, "failed to delete files in directory", t)
            throw t
        }
        repository.deleteAllNodes()
    }
}

