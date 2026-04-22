package com.melancholicbastard.handyasr.domain.node.usecases

import android.util.Log
import com.melancholicbastard.handyasr.domain.node.NodeRepository
import java.io.File

class DeleteNodeByIdUseCase(
    private val nodeRepository: NodeRepository
) {
    companion object {
        private const val TAG = "DeleteNodeByIdUseCase"
    }

    suspend operator fun invoke(id: Long) {
        try {
            val audioFilePath = nodeRepository.getNodeById(id)?.audioFileName
            audioFilePath?.let { it ->
                File(it).delete()
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error deleting audio file: $e")
        }
        nodeRepository.deleteNodeById(id)
    }
}

